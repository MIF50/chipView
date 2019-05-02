package com.mif50.chipview

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.view_chip.view.*

class ChipView: LinearLayout{

    // VARIABLE DEFINITIONS AND SETTERS FOR PROPERLY LOADING INFORMATION ---------------------------

    var text : String = ""

        /**
         * Sets the text label for this ChipView
         * @param value The string to set for this label
         */
        set(value) {
            field = value
            displayText()
        }

    var imageResource : Drawable? = null

        /**
         * Sets the image for this ChipView
         * @param value The drawable to set for this chip image
         */
        set(value) {
            field = value
            displayImage()
        }

    var imageURL : String? = null

        /**
         * Sets the image for this ChipView to be loaded from the given URL / path
         * @param value The URL path to an image to set for this chip
         */
        set(value) {
            field = value
            displayImage()
        }

    private var removeListener : OnChipRemovedListener? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        // Load the correct layout for this view
        inflate(context, R.layout.view_chip, this)

        // Display attributes only after the view has been inflated
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.ChipView, defStyleAttr, 0)

        try {

            // Set the initial text for this label
            text = attributes.getString(R.styleable.ChipView_text) ?: ""

            // We set the imageURL last since it takes precedence over a given resource.
            // We also still set the resource in case the user wants to use it as a default
            imageResource = attributes.getDrawable(R.styleable.ChipView_imageSrc)
            imageURL = attributes.getString(R.styleable.ChipView_imageURL)

            // We then initially hide the remove icon (a listener may be added later)
            displayRemoveIcon()

        } finally {
            attributes.recycle()
        }

    }




    // METHODS FOR PROPERLY DISPLAYING THE SET INFORMATION -----------------------------------------

    /**
     * Displays a new ChipView label by setting the layout's text and reloading the view.
     */
    private fun displayText() {
        textChip.text = text
        textChip.invalidate()
        textChip.requestLayout()
    }

    /**
     * Displays a new ChipView image by setting the layout's image and reloading the view.
     */
    private fun displayImage() {
        when {
            imageURL != null -> {
                imageChip.visibility = View.VISIBLE
                imageChip.load(imageURL!!)
            }
            imageResource != null -> {
                imageChip.visibility = View.VISIBLE
                imageChip.setImageDrawable(imageResource)
            }
            else -> {
                imageChip.visibility = View.GONE
                imageChip.setImageDrawable(null)
            }
        }
        imageChip.invalidate()
        imageChip.requestLayout()
    }

    /**
     * Displays the remove icon if a listener is available
     */
    private fun displayRemoveIcon() {
        if (removeListener != null) {
            closeChip.visibility = View.VISIBLE
            closeChip.setOnClickListener {
                removeListener!!.onRemove(this)
            }
        } else {
            closeChip.visibility = View.GONE
            closeChip.setOnClickListener {}
        }
        closeChip.invalidate()
        closeChip.requestLayout()
    }


    // METHODS AND INTERFACE FOR THE CHIP REMOVAL LISTENER -------------------------------------------------------
    interface OnChipRemovedListener {
        fun onRemove(v: View)
    }

    /**
     * Sets the OnClickListener or function to be called when the remove / close
     * button on the ChipView is clicked
     * @param listener The listener to be executed when the close button is clicked
     */
    fun setOnRemoveListener(listener: OnChipRemovedListener?) {
        removeListener = listener
        displayRemoveIcon()
    }

    fun setOnRemoveListener(listener: (v : View) -> Unit) {
        removeListener = object : OnChipRemovedListener {
            override fun onRemove(v: View) {
                listener(v)
            }
        }
        displayRemoveIcon()
    }


    private val Context.picasso: Picasso get() = Picasso.with(this)

    private fun CircleImageView?.load(path: String) {
        this!!.context.picasso.load(path).into(this)
    }

}