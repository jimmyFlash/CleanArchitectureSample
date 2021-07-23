
package com.jimmy.cleanarchitecture.presentation.reader

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import com.jimmy.cleanarchitecture.domain.Document
import com.jimmy.cleanarchitecture.R
import com.jimmy.cleanarchitecture.databinding.FragmentReaderBinding
import com.jimmy.cleanarchitecture.framework.MajesticViewModelFactory
import com.jimmy.cleanarchitecture.presentation.IntentUtil
import kotlinx.android.synthetic.main.fragment_reader.*

class ReaderFragment : Fragment(), LifecycleObserver {

  companion object {

    fun newInstance(document: Document) = ReaderFragment().apply {
      arguments = ReaderViewModel.createArguments(document) // create a bundle argument for instance
    }
  }

  private lateinit var viewModel: ReaderViewModel
  private lateinit var fragmentReaderBinding: FragmentReaderBinding

  private lateinit var openPdfContract : ActivityResultContract<Uri?, Uri?> // input-type, output-type
  private lateinit var openPdfCallback : ActivityResultCallback<Uri?> // output-type
  private lateinit var openPdfLauncher : ActivityResultLauncher<Uri?> // input-type

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View {

    fragmentReaderBinding = FragmentReaderBinding.inflate(inflater, container, false)

    // inject viewmodel from factory
    viewModel = ViewModelProvider(this, MajesticViewModelFactory)
      .get(ReaderViewModel::class.java)

    if(savedInstanceState == null) {
      // load pdf and display last bookmarked page from argument otherwise open last opened doc.
      viewModel.loadArguments(arguments)
    } else {
      // Recreating fragment after configuration change, reopen current page so it can be rendered again.
      viewModel.reopenPage()
    }

    openPdfContract = object:ActivityResultContract<Uri?, Uri?>(){
      override fun createIntent(context: Context, input: Uri?): Intent {
        return IntentUtil.createOpenIntent()
      }

      override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (resultCode == Activity.RESULT_OK && intent != null) {
          intent.data
        } else {
          null
        }
      }
    }

    openPdfCallback = ActivityResultCallback<Uri?> { pdfUri ->
      if (pdfUri != null) {
        viewModel.openDocument(pdfUri)
      }
    }
    openPdfLauncher = registerForActivityResult(openPdfContract, openPdfCallback)

    return fragmentReaderBinding.root
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  fun onCreated() {

    val adapter = BookmarksAdapter {
      viewModel.openBookmark(it)
    }
    bookmarksRecyclerView.adapter = adapter

    openPdfContract.createIntent(requireContext(), null )

    viewModel.document.observe(viewLifecycleOwner, {
      if (it == Document.EMPTY) {
        // Show file picker action.
        openPdfLauncher.launch(null)
      }
    })

    viewModel.bookmarks.observe(viewLifecycleOwner, {
      adapter.update(it)
    })

    viewModel.isBookmarked.observe(viewLifecycleOwner, {
      val bookmarkDrawable = if (it) R.drawable.ic_bookmark else R.drawable.ic_bookmark_border
      tabBookmark.setCompoundDrawablesWithIntrinsicBounds(0, bookmarkDrawable, 0, 0)
    })

    viewModel.isInLibrary.observe(viewLifecycleOwner, {
      val libraryDrawable = if(it) R.drawable.ic_library else R.drawable.ic_library_border
      tabLibrary.setCompoundDrawablesRelativeWithIntrinsicBounds(0, libraryDrawable, 0, 0)
    })

    // open last bookmarked page or the 1st page if no bookmarks found
    viewModel.currentPage.observe(viewLifecycleOwner, { showPage(it) })
    viewModel.hasNextPage.observe(viewLifecycleOwner, { tabNextPage.isEnabled = it })
    viewModel.hasPreviousPage.observe(viewLifecycleOwner, { tabPreviousPage.isEnabled = it })

    tabBookmark.setOnClickListener { viewModel.toggleBookmark() }
    tabLibrary.setOnClickListener { viewModel.toggleInLibrary() }
    tabNextPage.setOnClickListener { viewModel.nextPage() }
    tabPreviousPage.setOnClickListener { viewModel.previousPage() }
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    lifecycle.addObserver(this)
  }

  override fun onDetach() {
    super.onDetach()
    lifecycle.removeObserver(this)
  }

  /**
   * display pdf page in renderer and allow navigation controls
   */
  private fun showPage(page: PdfRenderer.Page) {
    iv_page.visibility = View.VISIBLE
    pagesTextView.visibility = View.VISIBLE
    tabPreviousPage.visibility = View.VISIBLE
    tabNextPage.visibility = View.VISIBLE

    // remove and free memory of any loaded pdf page
    if (iv_page.drawable != null) {
      (iv_page.drawable as BitmapDrawable).bitmap.recycle()
    }

    // get display screen size and store in to Point object
    val size = Point()
    @Suppress("DEPRECATION")
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
      activity?.display?.getRealSize(size)
    }else {
      activity?.windowManager?.defaultDisplay?.getSize(size)
    }

    // calculate the fit screen size for rendered page
    val pageWidth = size.x
    val pageHeight = page.height * pageWidth / page.width

    val bitmap = Bitmap.createBitmap(
        pageWidth,
        pageHeight,
        Bitmap.Config.ARGB_8888)

    // render page
    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
    iv_page.setImageBitmap(bitmap)

    // display the page number / page count in bottom navigation
    pagesTextView.text = getString(
        R.string.page_navigation_format,
        page.index + 1,
        viewModel.renderer.value?.pageCount
    )

    // close renderer
    page.close()
  }

}
