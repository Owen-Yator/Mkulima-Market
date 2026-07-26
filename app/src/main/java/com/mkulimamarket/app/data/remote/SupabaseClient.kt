package com.mkulimamarket.app.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.okhttp.OkHttp

object SupabaseClient {

    private const val SUPABASE_URL =
        "https://hyfcguaadrnuxxffxekn.supabase.co"

    private const val SUPABASE_KEY =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imh5ZmNndWFhZHJudXh4ZmZ4ZWtuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODUwMzUzODYsImV4cCI6MjEwMDYxMTM4Nn0.9MDdB9uUNFgi6xtOkQMPkcgFJ6BlivJg94KPfG7_22Y"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)
        httpEngine = OkHttp.create()
    }
}
