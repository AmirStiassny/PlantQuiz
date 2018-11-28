package com.green.plantquiz.Controller

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.green.plantquiz.R
import com.green.plantquiz.model.DownloadingObject
import com.green.plantquiz.model.ParsePlantUtility
import com.green.plantquiz.model.Plant

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private var cameraButton : Button? = null
    private var GalleryButton : Button? = null
    private var imageTaken : ImageView? = null

    val OPEN_CAMERA_BUTTON_REQUEST_ID = 1000
    val OPEN_GALLERY_BUTTON_REQUEST_ID = 2000

    var correctAnswerIndex : Int = 0
    var correctPlant : Plant? = null

    var numberOfTimesUserAnsweredCorrectly: Int = 0
    var numberOfTimesUserAnsweredInCorrectly: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        setProgressBar(false)

        displayUIWidgets(false)

        /*YoYo.with(Techniques.Pulse)
            .duration(700)
            .repeat(5)
            .playOn(btnNextPlant)*/


        //cameraButton = findViewById(R.id.btnOpenCamera)
        GalleryButton = findViewById(R.id.btnOpenPhotoGallery)

        imageTaken = findViewById(R.id.imgTaken)

        /*cameraButton?.setOnClickListener((View.OnClickListener {

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent,OPEN_CAMERA_BUTTON_REQUEST_ID)

        }))

        GalleryButton?.setOnClickListener((View.OnClickListener {

            val galleyIntent =Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleyIntent,OPEN_GALLERY_BUTTON_REQUEST_ID)

        }))*/

        ///see the next plant
        btnNextPlant.setOnClickListener {

            if (checkForInternetConnection()) {

                setProgressBar(true)

                progressBar.setVisibility(View.VISIBLE)

                try {

                    val innerClassObject = DownloadingPlantTask()
                    innerClassObject.execute()

                }catch (e : Exception){

                    e.printStackTrace()
                }
                /*button1.setBackgroundColor(Color.LTGRAY)
                button2.setBackgroundColor(Color.LTGRAY)
                button3.setBackgroundColor(Color.LTGRAY)
                button4.setBackgroundColor(Color.LTGRAY)*/

                var gradientColors: IntArray = IntArray(2)
                gradientColors.set(0, Color.parseColor("#FFFF66"))
                gradientColors.set(1, Color.parseColor("#FF0008"))
                var gradientDrawable: GradientDrawable =
                    GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        gradientColors)
                var convertDipValue = dipToFloat(this@MainActivity, 50f)
                gradientDrawable.setCornerRadius(convertDipValue)
                gradientDrawable.setStroke(5, Color.parseColor("#ffffff"))


                button1.setBackground(gradientDrawable)
                button2.setBackground(gradientDrawable)
                button3.setBackground(gradientDrawable)
                button4.setBackground(gradientDrawable)

            }


        }

        ////the end of the onCreate Method
    }

    fun dipToFloat(context: Context, dipValue: Float): Float {

        val metrics: DisplayMetrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics)

    }

    fun button1IsClicked(buttonView: View){

        specipyRightOrWrongAnswer(0)
    }
    fun button2IsClicked(buttonView: View){

        specipyRightOrWrongAnswer(1)
    }
    fun button3IsClicked(buttonView: View){

        specipyRightOrWrongAnswer(2)
    }
    fun button4IsClicked(buttonView: View){

        specipyRightOrWrongAnswer(3)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == OPEN_CAMERA_BUTTON_REQUEST_ID){
            if(resultCode == Activity.RESULT_OK){

                val imageData = data?.getExtras()?.get("data") as Bitmap

                imageTaken?.setImageBitmap(imageData)

            }
        }

        if(requestCode == OPEN_GALLERY_BUTTON_REQUEST_ID){
            if(resultCode == Activity.RESULT_OK){

                val contentURI = data?.getData()
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,contentURI)
                imgTaken.setImageBitmap(bitmap)

            }
        }
    }

    inner class DownloadingPlantTask: AsyncTask<String, Int, List<Plant>>() {

        override fun doInBackground(vararg params: String?): List<Plant>? {


            // Can access background thread. Not user interface thread

//           val downloadingObject: DownloadingObject = DownloadingObject()
//           var jsonData =  downloadingObject.downloadJSONDataFromLink(
//                   "http://plantplaces.com/perl/mobile/flashcard.pl")
//
//           Log.i("JSON", jsonData)

            val parsePlant = ParsePlantUtility()



            return parsePlant.parsePlantObjectsFromJSONData()

        }


        override fun onPostExecute(result: List<Plant>?) {
            super.onPostExecute(result)

            // Can access user interface thread. Not background thread

            var numberOfPlants = result?.size ?: 0

            if (numberOfPlants > 0) {

                var randomPlantIndexForButton1: Int = (Math.random() * result!!.size).toInt()
                var randomPlantIndexForButton2: Int = (Math.random() * result!!.size).toInt()
                var randomPlantIndexForButton3: Int = (Math.random() * result!!.size).toInt()
                var randomPlantIndexForButton4: Int = (Math.random() * result!!.size).toInt()

                var allRandomPlants = ArrayList<Plant>()
                allRandomPlants.add(result.get(randomPlantIndexForButton1))
                allRandomPlants.add(result.get(randomPlantIndexForButton2))
                allRandomPlants.add(result.get(randomPlantIndexForButton3))
                allRandomPlants.add(result.get(randomPlantIndexForButton4))

                button1.text = result.get(randomPlantIndexForButton1).toString()
                button2.text = result.get(randomPlantIndexForButton2).toString()
                button3.text = result.get(randomPlantIndexForButton3).toString()
                button4.text = result.get(randomPlantIndexForButton4).toString()

                correctAnswerIndex = (Math.random() * allRandomPlants.size).toInt()
                correctPlant = allRandomPlants.get(correctAnswerIndex)

                val downloadingImageTask = DownloadingImageTask()
                downloadingImageTask.execute(allRandomPlants.get(correctAnswerIndex).picture_name)
            }



        }
    }

    /*override fun onStart() {
        super.onStart()

        Toast.makeText(this,"the on Start start",Toast.LENGTH_LONG).show()

    }

    override fun onResume() {
        super.onResume()

        checkForInternetConnection()

    }

    override fun onPause() {
        super.onPause()

        Toast.makeText(this,"the on pause start",Toast.LENGTH_LONG).show()

    }

    override fun onStop() {
        super.onStop()

        Toast.makeText(this,"the on stop start",Toast.LENGTH_LONG).show()
    }

    override fun onRestart() {
        super.onRestart()

        Toast.makeText(this,"the on restart start",Toast.LENGTH_LONG).show()

    }

    override fun onDestroy() {
        super.onDestroy()

        Toast.makeText(this,"the on destroy start",Toast.LENGTH_LONG).show()

    }*/

    /*fun imageViewIsClicked(view : View){

        val randomNumber : Int = (Math.random() * 6 ).toInt() + 1

        when(randomNumber){

            1->btnOpenCamera.setBackgroundColor(Color.BLUE)

        }

    }*/

    ////Check internet Connection
    private fun checkForInternetConnection () : Boolean {

        val connectActivityManager = this.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = connectActivityManager.activeNetworkInfo
        val isDeviceConnectedToInternet = networkInfo != null && networkInfo.isConnectedOrConnecting

        if(isDeviceConnectedToInternet) {

            return true

        }else{

            createAlert()
            return false
        }

    }

    private fun createAlert() {

        val alertDialog : AlertDialog = AlertDialog.Builder(this@MainActivity).create()
        alertDialog.setTitle("Network Error")
        alertDialog.setMessage("Please check for internet connection")
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"Ok") {
                dialog: DialogInterface?, which: Int ->

            startActivity(Intent(Settings.ACTION_SETTINGS))

        }

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE , "Cancel") {

                dialog: DialogInterface?, which: Int ->

            Toast.makeText(this@MainActivity , "You must be connected to the internet"
                , Toast.LENGTH_LONG).show()
            finish()

        }

        alertDialog.show()

    }

    ///specify the right or wrong answer
    private fun specipyRightOrWrongAnswer(userGuess : Int){

        when(correctAnswerIndex){

            0->button1.setBackgroundColor(Color.CYAN)
            1->button2.setBackgroundColor(Color.CYAN)
            2->button3.setBackgroundColor(Color.CYAN)
            3->button4.setBackgroundColor(Color.CYAN)
        }

        if(userGuess == correctAnswerIndex){

            txtState.setText("Right Answer")
            numberOfTimesUserAnsweredCorrectly++
            txtRightAnswers.setText("$numberOfTimesUserAnsweredCorrectly")

        }else{

            var correctPlantName = correctPlant.toString()
            txtState.setText("Wrong. Choose : $correctPlantName")
            numberOfTimesUserAnsweredInCorrectly++
            txtWrongAnswers.setText("$numberOfTimesUserAnsweredInCorrectly")
        }
    }

    ////downloading image process
    inner class DownloadingImageTask : AsyncTask<String, Int, Bitmap?>(){
        override fun doInBackground(vararg pictureName: String?): Bitmap? {

            try {

                val downloadingObject = DownloadingObject()
                val PlantBitmap: Bitmap? = downloadingObject.downloadPlantPicture(pictureName[0])

                return PlantBitmap

            } catch (e: Exception) {

                e.printStackTrace()
            }

            return null
        }
        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)

            setProgressBar(false)
            displayUIWidgets(true)
            playAnimationOnView(imgTaken, Techniques.Tada)
            playAnimationOnView(button1,Techniques.RollIn)
            playAnimationOnView(button2,Techniques.RollIn)
            playAnimationOnView(button3,Techniques.RollIn)
            playAnimationOnView(button4,Techniques.RollIn)
            playAnimationOnView(txtState,Techniques.FadeIn)
            playAnimationOnView(txtRightAnswers,Techniques.BounceInRight)
            playAnimationOnView(txtWrongAnswers,Techniques.BounceInLeft)


            imgTaken.setImageBitmap(result)
        }

    }

    // ProgressBar Visibility
    private fun setProgressBar(show: Boolean) {

        if (show) {

            linearLayoutProgress.setVisibility(View.VISIBLE)
            progressBar.setVisibility(View.VISIBLE)  //To show ProgressBar
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        } else if (!show) {

            linearLayoutProgress.setVisibility(View.GONE)
            progressBar.setVisibility(View.GONE)     // To Hide ProgressBar
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    // Set Visibility of ui widgets
    private fun displayUIWidgets(display: Boolean) {

        if (display) {

            imgTaken.setVisibility(View.VISIBLE)
            button1.setVisibility(View.VISIBLE)
            button2.setVisibility(View.VISIBLE)
            button3.setVisibility(View.VISIBLE)
            button4.setVisibility(View.VISIBLE)
            txtState.setVisibility(View.VISIBLE)
            txtWrongAnswers.setVisibility(View.VISIBLE)
            txtRightAnswers.setVisibility(View.VISIBLE)

        } else if (!display) {

            imgTaken.setVisibility(View.INVISIBLE)
            button1.setVisibility(View.INVISIBLE)
            button2.setVisibility(View.INVISIBLE)
            button3.setVisibility(View.INVISIBLE)
            button4.setVisibility(View.INVISIBLE)
            txtState.setVisibility(View.INVISIBLE)
            txtWrongAnswers.setVisibility(View.INVISIBLE)
            txtRightAnswers.setVisibility(View.INVISIBLE)
        }
    }

    // Playing Animations
    private fun playAnimationOnView(view: View?, technique: Techniques) {

        YoYo.with(technique)
            .duration(700)
            .repeat(0)
            .playOn(view)

    }

}
