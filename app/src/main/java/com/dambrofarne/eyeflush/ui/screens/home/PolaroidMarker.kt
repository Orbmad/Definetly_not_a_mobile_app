package com.dambrofarne.eyeflush.ui.screens.home

//import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.OverlayWithIW

class PolaroidMarker(
    private val id: String,
    private val position: GeoPoint,
    private val photoFrame: Drawable,
    private val photoCount: Int = 1,
    private val textColor: Int = Color.BLACK,
    private val paintColor: Int = Color.WHITE,
    private val borderColor: Int = Color.GRAY
) : OverlayWithIW() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = paintColor
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = textColor
        textSize = 32f
        typeface = Typeface.DEFAULT_BOLD
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = borderColor
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    private val overlayWidth = 200
    private val overlayHeight = 250

    private var onMarkerClicked: (() -> Unit)? = null

    fun setMarkerClickedAction(action: (() -> Unit)) {
        onMarkerClicked = action
    }

    fun getID(): String {
        return id
    }

    override fun draw(canvas: Canvas, mapView: MapView, shadow: Boolean) {
        if (!isEnabled) return
        if (shadow) return

        val projection = mapView.projection
        val screenPoint = projection.toPixels(position, null)

        val left = screenPoint.x - overlayWidth / 2
        val top = screenPoint.y - overlayHeight
        val right = screenPoint.x + overlayWidth / 2
        val bottom = screenPoint.y

        val rect = Rect(left, top, right, bottom)

        //Create photo bitmap from drawable
        val photoBitmapFromDrawable = (photoFrame as BitmapDrawable).bitmap
        val resizedBitmap = resizeBitmap(photoBitmapFromDrawable)

        // Draw polaroid background
        canvas.drawRect(rect, paint)
        canvas.drawRect(rect, borderPaint)

        // Draw photo
        val photoRect = Rect(
            left + 10,
            top + 10,
            right - 10,
            top + overlayWidth - 10
        )
        canvas.drawBitmap(resizedBitmap, null, photoRect, null)

        // Draw number of likes
        val likeText = "$photoCount"
        val textWidth = textPaint.measureText(likeText)
        canvas.drawText(
            likeText,
            screenPoint.x - textWidth / 2,
            bottom - 20f,
            textPaint
        )
    }

    override fun onSingleTapConfirmed(e: MotionEvent, mapView: MapView): Boolean {
        val projection = mapView.projection
        val screenPoint = projection.toPixels(position, null)

        val left = screenPoint.x - overlayWidth / 2
        val top = screenPoint.y - overlayHeight
        val right = screenPoint.x + overlayWidth / 2
        val bottom = screenPoint.y

        val touchX = e.x.toInt()
        val touchY = e.y.toInt()

        if (touchX in left..right && touchY in top..bottom) {
            // Handle marker tap - navigate to detail page
            onMarkerClicked?.invoke()
            return true
        }

        return false
    }

    private fun resizeBitmap(bitmap: Bitmap): Bitmap {
        val maxWidth = overlayWidth - 20
        val maxHeight = overlayHeight - 40
        val width = bitmap.width
        val height = bitmap.height

        val ratioBitmap = width.toFloat() / height.toFloat()
        val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

        var finalWidth = maxWidth
        var finalHeight = maxHeight

        if (ratioMax > ratioBitmap) {
            finalWidth = ((maxHeight.toFloat() * ratioBitmap).toInt())
        } else {
            finalHeight = ((maxWidth.toFloat() / ratioBitmap).toInt())
        }

        return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
    }
}
