package com.example.whiteboard

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.whiteboard.databinding.ActivityMainBinding
import com.example.whiteboard.utils.FileUtils
import com.example.whiteboard.viewModel.BoardViewModel
import com.example.whiteboard.viewModel.GenericViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: BoardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)


        val whiteBoardFactory = GenericViewModelFactory { BoardViewModel() }
        viewModel =
            ViewModelProvider(this@MainActivity, whiteBoardFactory)[BoardViewModel::class.java]
        binding.drawingView.setViewModel(viewModel)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        setupUI()

    }


    /**
     * @author Pradeep Prajapati
     * method for set up ui and handle onclick event
     * */
    private fun setupUI() {
        binding.coloricon.setOnClickListener {
            showColorPopup(it)
        }

        binding.strokeIcon.setOnClickListener {
            showStrokePopup(it)
        }

        binding.shapeIcon.setOnClickListener {
            showShapePopup(it)
        }

        binding.eraserIcon.setOnClickListener {
            viewModel.toggleEraser()
            binding.drawingView.invalidate()

            viewModel.strokes.observe(this) { binding.drawingView.invalidate() }
            viewModel.shapes.observe(this) { binding.drawingView.invalidate() }
            viewModel.texts.observe(this) { binding.drawingView.invalidate() }
        }

        binding.textIcon.setOnClickListener {
            ShowDialogBoxForTExt()
        }

        binding.saveIcon.setOnClickListener {
            FileUtils.saveWhiteboard(this, viewModel.getWhiteboardData())
        }

        binding.saveImageIcon.setOnClickListener {
            val bitmap = binding.drawingView.exportToBitmap()
            val uri = viewModel.saveBitmapToGallery(this, bitmap)
            if (uri != null) {
                Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show()
            }
        }

        binding.clearIcon.setOnClickListener {
            viewModel.clearAll()
        }


    }


    /**
     * @author Pradeeep Prajapati
     * method for showing list of colors in pop up
     * */
    private fun showColorPopup(anchor: View) {
        val popup = PopupMenu(this, anchor)
        popup.menu.apply {
            add("Red").setOnMenuItemClickListener {
                viewModel.setColor("#FF0000"); true
            }
            add("Blue").setOnMenuItemClickListener {
                viewModel.setColor("#0000FF"); true
            }
            add("Green").setOnMenuItemClickListener {
                viewModel.setColor("#00FF00"); true
            }
            add("Black").setOnMenuItemClickListener {
                viewModel.setColor("#000000"); true
            }
        }
        popup.show()
    }


    /**
     * @author Pradeeep Prajapati
     * method for showing list of stroke in pop up
     * */
    private fun showStrokePopup(anchor: View) {
        val popup = PopupMenu(this, anchor)
        popup.menu.apply {
            add("Thin").setOnMenuItemClickListener {
                viewModel.setWidth(4f); true
            }
            add("Medium").setOnMenuItemClickListener {
                viewModel.setWidth(8f); true
            }
            add("Thick").setOnMenuItemClickListener {
                viewModel.setWidth(12f); true
            }
        }
        popup.show()
    }

    /**
     * @author Pradeeep Prajapati
     * method for showing list of shapes in pop up
     * */
    private fun showShapePopup(anchor: View) {
        val popup = PopupMenu(this, anchor)
        popup.menu.apply {
            add("Rectangle").setOnMenuItemClickListener {
                viewModel.setShapeMode("rectangle"); true
            }
            add("Circle").setOnMenuItemClickListener {
                viewModel.setShapeMode("circle"); true
            }
        }
        popup.show()
    }


    private fun ShowDialogBoxForTExt() {
        val editText = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Please Enter Text")
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->

                viewModel.insertText(editText.text.toString(), 100f to 200f)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}