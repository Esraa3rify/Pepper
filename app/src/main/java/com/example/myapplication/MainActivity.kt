package com.example.myapplication

import android.content.ContentValues.TAG
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.actuation.Animate
import com.aldebaran.qi.sdk.`object`.actuation.Animation
import com.aldebaran.qi.sdk.`object`.conversation.BodyLanguageOption
import com.aldebaran.qi.sdk.`object`.conversation.Phrase
import com.aldebaran.qi.sdk.`object`.conversation.Say
import com.aldebaran.qi.sdk.`object`.locale.Language
import com.aldebaran.qi.sdk.`object`.locale.Locale
import com.aldebaran.qi.sdk.`object`.locale.Region
import com.aldebaran.qi.sdk.builder.AnimationBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.design.activity.RobotActivity

class MainActivity : RobotActivity(), RobotLifecycleCallbacks {

    private var animate: Animate? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Register the RobotLifecycleCallbacks to this Activity.
        QiSDK.register(this, this)
    }

    override fun onDestroy() {
        // Unregister the RobotLifecycleCallbacks for this Activity.
        QiSDK.unregister(this, this)
        super.onDestroy()
    }

    override fun onRobotFocusGained(qiContext: QiContext) {
        // The robot focus is gained.

        val testBtn = findViewById<Button>(R.id.testButton)
        testBtn.setOnClickListener {

            // Create a phrase.
            val phrase: Phrase = Phrase("Hello To Proven!")

            //determine language
            val locale: Locale = Locale(Language.ENGLISH, Region.CANADA);

            // Build the action.
            val say: Say = SayBuilder.with(qiContext).withPhrase(phrase)
                //prevent robot from gestures
                .withBodyLanguageOption(BodyLanguageOption.DISABLED).build()
            // Run the action synchronously.
            say.run()

            Toast.makeText(this, "The Robot succeeded to Say Hello To Proven!", Toast.LENGTH_LONG)
                .show()

            Log.i(TAG, "The Robot succeeded to Say Hello To Proven!")

        }
        val animation: com.aldebaran.qi.sdk.`object`.actuation.Animation =
            AnimationBuilder.with(qiContext) // Create the builder with the context.
                .withResources(R.raw.hello_a006) // Set the animation resource.
                .build() // Build the animation.


        // Add an on started listener to the animate action.
        animate?.addOnStartedListener { Log.i(TAG, "Animation started.") }

        // Run the animate action asynchronously.
        val animateFuture: Future<Void>? = animate?.async()?.run()

        // Add a lambda to the action execution.
        animateFuture?.thenConsume { future ->
            if (future.isSuccess) {
                Log.i(TAG, "Animation finished with success.")
            } else if (future.hasError()) {
                Log.e(TAG, "Animation finished with error.", future.error)

            }
        }
    }


    override fun onRobotFocusLost() {
        // The robot focus is lost.

        // Remove on started listeners from the animate action.
        animate?.removeAllOnStartedListeners()
    }

    override fun onRobotFocusRefused(reason: String) {
        // The robot focus is refused.
    }

}

