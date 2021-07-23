
package com.jimmy.cleanarchitecture.presentation.library

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.jimmy.cleanarchitecture.databinding.FragmentLibraryBinding

import com.jimmy.cleanarchitecture.framework.MajesticViewModelFactory
import com.jimmy.cleanarchitecture.presentation.IntentUtil.createOpenIntent
import com.jimmy.cleanarchitecture.presentation.MainActivityDelegate
import kotlinx.android.synthetic.main.fragment_library.*

class LibraryFragment : Fragment(), LifecycleObserver {

  companion object {
      fun newInstance() = LibraryFragment()
  }

  private lateinit var viewModel: LibraryViewModel

  private lateinit var mainActivityDelegate: MainActivityDelegate

  private var fragmentLibraryBinding: FragmentLibraryBinding? = null

  private val binding
      get() = fragmentLibraryBinding!!

  private lateinit var openPdfContract : ActivityResultContract<Uri?, Uri?> // input-type, output-type
  private lateinit var openPdfCallback : ActivityResultCallback<Uri?> // output-type
  private lateinit var openPdfLauncher : ActivityResultLauncher<Uri?> // input-type


  override fun onAttach(context: Context) {
    super.onAttach(context)
    // check parent activity is MainActivityDelegate type
    try {
      mainActivityDelegate = context as MainActivityDelegate
    } catch (e: ClassCastException) {
      throw ClassCastException()
    }
    lifecycle.addObserver(this)
  }

   @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
   fun onCreated() {
    Log.d(LibraryFragment::class.java.simpleName, "reached state created")
     val adapter = DocumentsAdapter(glide = Glide.with(this)) {
       mainActivityDelegate.openDocument(it) // lambda call main activity method to load reader fragment
     }
     documentsRecyclerView.adapter = adapter

       // inject viewmodel from factory
     viewModel = ViewModelProvider(this, MajesticViewModelFactory)
       .get(LibraryViewModel::class.java)
     viewModel.documents.observe(viewLifecycleOwner, { adapter.update(it) })
     viewModel.loadDocuments() // load all documents stored in db

     openPdfContract.createIntent(requireContext(), null )

     fab.setOnClickListener {
       openPdfLauncher.launch(null) // launch system picker for loading pdf from device storage
     }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View {
    fragmentLibraryBinding = FragmentLibraryBinding.inflate(layoutInflater,
      container, false)

      openPdfContract = object:ActivityResultContract<Uri?, Uri?>(){
          override fun createIntent(context: Context, input: Uri?): Intent {
              return createOpenIntent()
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
              viewModel.addDocument(pdfUri)
          }
      }
      openPdfLauncher = registerForActivityResult(openPdfContract, openPdfCallback)
    return binding.root
  }

  override fun onDetach() {
    super.onDetach()
    lifecycle.removeObserver(this)
  }
  override fun onDestroy() {
    super.onDestroy()
    fragmentLibraryBinding = null
  }

}
