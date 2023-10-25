package com.bingyan.bbhust.utils

const val H5_BASEURL = "https://bbhust-mobile.hust.online"

//const val H5_BASEURL = "http://10.0.0.2:5173"
val ENV_PARAM = if (SP.isReleaseServer) "?dev=false" else "?dev=true"
val LOGIN_URL = "$H5_BASEURL/accounts/login$ENV_PARAM"
val REGISTER_URL = "$H5_BASEURL/accounts/register$ENV_PARAM"
val MANAGE_URL = "$H5_BASEURL/accounts$ENV_PARAM"
val PRIVATE_SETTING_URL = "$H5_BASEURL/accounts/private_setting$ENV_PARAM"

