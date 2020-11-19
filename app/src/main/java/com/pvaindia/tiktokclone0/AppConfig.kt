package com.pvaindia.tiktokclone0

class AppConfig {

    companion object {

        @JvmField
        val API_URL = "https://apis.argear.io/api/v3/e42385f098c41e4e368f68ab/"
        @JvmField
        val API_KEY = "e42385f098c41e4e368f68ab"
        @JvmField
        val SECRET_KEY = "6c8c8c19c26f356170ddf0950ae379ad7217cff8dd3efbc8bc77b13e579cb52b"
        @JvmField
        val AUTH_KEY = "U2FsdGVkX19qheu8x7loAK0UTkidoCrRYJajOacR+XoJP9r83y41JEjDqJl6EioIIFn4mYqKMXh1vM/2MxP3SA=="

        // preference
        @JvmField
        val USER_PREF_NAME = BuildConfig.APPLICATION_ID + ".Preference"
        @JvmField
        val USER_PREF_NAME_FILTER = BuildConfig.APPLICATION_ID + ".ARGearFilter.Preference"
        @JvmField
        val USER_PREF_NAME_STICKER = BuildConfig.APPLICATION_ID + ".ARGearItem.Preference"

        // camera
        // 1: CAMERA_API_1, 2: CAMERA_API_2
        @JvmField
        val USE_CAMERA_API = 1

        // region - beauty sample
        @JvmField
        val BEAUTY_TYPE_INIT_VALUE = floatArrayOf(
            10f,     //VLINE
            90f,     //ACE_SLIM
            55f,     //JAW
            -50f,    //CHIN
            5f,      //EYE
            -10f,    //EYE_GAP
            0f,      //NOSE_LINE
            35f,     //NOSE_SIDE
            30f,     //NOSE_LENGTH
            -35f,    //MOUTH_SIZE
            0f,      //EYE_BACK
            0f,      //EYE_CORNER
            0f,      //LIP_SIZE
            50f,     //SKIN
            0f,      //DARK_CIRCLE
            0f       //MOUTH_WRINKLE
        )

        @JvmField
        val BASIC_BEAUTY_1 = floatArrayOf(20f, 10f, 45f, 45f, 5f, -10f, 40f, 20f, 15f, 0f, 0f, 0f, 0f, 50f, 0f, 0f)

        @JvmField
        val BASIC_BEAUTY_2 = floatArrayOf(10f, 90f, 55f, -50f, 5f, -10f, 0f, 35f, 30f, -35f, 0f, 0f, 0f, 50f, 0f, 0f)

        @JvmField
        val BASIC_BEAUTY_3 = floatArrayOf(25f, 20f, 50f, -25f, 25f, -10f, 30f, 40f, 90f, 0f, 0f, 0f, 0f, 50f, 0f, 0f)
        // endregion
    }
}