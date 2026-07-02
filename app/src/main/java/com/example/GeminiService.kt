package com.example

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// --- Moshi Models for Gemini ---

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @Json(name = "contents") val contents: List<GeminiContent>,
    @Json(name = "generationConfig") val generationConfig: GeminiGenerationConfig? = null,
    @Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @Json(name = "parts") val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @Json(name = "text") val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    @Json(name = "temperature") val temperature: Float? = null,
    @Json(name = "responseMimeType") val responseMimeType: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContent
)

// --- Archive Parsing Models ---

@JsonClass(generateAdapter = true)
data class ArchiveResponse(
    @Json(name = "work_metadata") val workMetadata: WorkMetadataResponse,
    @Json(name = "segmented_text") val segmentedText: List<VerseResponse>,
    @Json(name = "oral_tradition_metadata") val oralTraditionMetadata: OralTraditionMetadataResponse? = null
)

@JsonClass(generateAdapter = true)
data class WorkMetadataResponse(
    @Json(name = "title_arabic") val titleArabic: String,
    @Json(name = "title_transliteration") val titleTransliteration: String,
    @Json(name = "canonical_index_number") val canonicalIndexNumber: Int,
    @Json(name = "theological_category") val theologicalCategory: String
)

@JsonClass(generateAdapter = true)
data class VerseResponse(
    @Json(name = "verse_index") val verseIndex: Int,
    @Json(name = "arabic_text") val arabicText: String,
    @Json(name = "latin_transliteration") val latinTransliteration: String,
    @Json(name = "english_translation") val englishTranslation: String,
    @Json(name = "wolof_translation_standard") val wolofTranslationStandard: String,
    @Json(name = "wolofal_ajami_script") val wolofalAjamiScript: String? = null,
    @Json(name = "memorization_loop_count") val memorizationLoopCount: Int? = null,
    @Json(name = "suggested_tasbih_target") val suggestedTasbihTarget: Int? = null
)

@JsonClass(generateAdapter = true)
data class OralTraditionMetadataResponse(
    @Json(name = "chanted_style_reference") val chantedStyleReference: String? = null,
    @Json(name = "singing_melody_air_description") val singingMelodyAirDescription: String? = null,
    @Json(name = "historical_context_notes") val historicalContextNotes: String? = null
)

// --- Retrofit Setup ---

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        retrofit.create(GeminiApiService::class.java)
    }

    val moshiParser: Moshi = moshi
}

object GeminiService {
    
    // System Instructions parsed directly from PDF Section 1
    private const val SYSTEM_INSTRUCTION = """
You are the El Hajj Malick Sy Digital Archive AI Engine (EHMS-Engine), the specialized core computational intelligence of the University of El Hajj Malick Sy of Tivaouane. Your purpose is to serve as the definitive academic, linguistic, and spiritual parser for the canonical literary corpus of El Hajj Malick Sy (1855–1922). You are programmed with deep expertise in Malikite jurisprudence, Ash'arite theology, orthodox Tijaniyya Sufism, and the specific historical-linguistic traditions of the Tivaouane Hadra.

Your primary task is to generate, verify, and format multi-layered digital editions of El Hajj Malick Sy's works. For any selected text, you must produce synchronized, context-aware translations across four target representations: Classical Arabic, Latin Transliteration, English, and Wolof (incorporating Wolofal/Ajami notation where relevant).

Canonical Corpus Knowledge Base
You must strictly recognize and structure outputs for the 17 key works indexed in our canonical library:
1. Taysir, Wassilatul muna: The celebrated poetic prayer woven around the 99 Divine Names (Asma al-Husna).
2. Ifham al munkiru jaani: The major apologia defending the orthodox jurisprudential foundations of the Tijaniyya order.
3. Khilaçu ez-Zahab: The definitive rhyming poetic biography (Sira) of the Prophet Muhammad.
4. Sharh Khilaçu ez-Zahab: The author's own prose commentary explaining the historical-theological contexts of Khilaçu ez-Zahab.
5. Zajr ul Qulub: A collection of highly expressive, hortatory poetry focused on spiritual purification and asceticism.
6. Adab ul Masjid: A practical liturgical poem detailing the rules of conduct and spiritual etiquette inside the mosque.
7. Al Hidayat ul Wildan: A dense theological treatise teaching Islamic creed according to orthodox Ash'arite formulations.
8. Faakihatul Tullab: A foundational practical manual explaining the litanies, rituals, and ethical behavior required in the Tijaniyya path.
9. Khutbatul Jumu'a: Formal Arabic sermon outlines composed for the Friday congregational prayers.
10. Khutbatul 'I'd: Formal sermon outlines written for the Eid al-Fitr and Eid al-Adha community celebrations.
11. Kifayat ar-raghibîn: A detailed prose work compiling Malikite jurisprudential rulings and spiritual advice.
12. Risalatu latif: A short, accessible epistle conveying guidance on personal ethics and social morality.
13. Hurufu salatul fatihi: An esoteric study of the structural and spiritual letters of the Salat al-Fatih.
14. Majmu -r-atu dua-u al wazifati: The organized manual of supplications recited during the daily collective Wazifa.
15. Khassidatu riyu zaman - Nunya: A classical poetic ode rhyming in the letter Nun, containing social critiques and moral solutions.
16. Dua-u rufat: A specific liturgical supplication written for spiritual elevation, preservation, and divine protection.
17. Wasilatul muharabin: A defensive devotional poem designed as a spiritual shield for physical and metaphysical protection.

Cross-Linguistic and Phonetic Protocols
For every request, you must parse the source text through these four linguistic layers:
1. Arabic Text Layer: Render fully vocalized, high-definition Unicode Arabic text. Ensure absolute orthographic accuracy; do not alter vowel markings (harakat), as they alter liturgical validity.
2. Latin Transliteration Layer: Produce a standardized phonetic transliteration adapted from the International Journal of Middle East Studies (IJMES) schema. Long vowels must utilize macron markers (ā, ī, ū). Pharyngealized/velarized consonants must utilize sub-dot markers (ṣ, ḍ, ṭ, ẓ, ḥ).
3. English Translation Layer: Provide a philosophically precise translation. Do not use overly simplistic translations. For Ash'arite Theology: Translate terms like wujūd as "Existence," sifāt as "Divine Attributes," and mukhalafatu lil-hawadith as "absolute transcendence over created things". For Sufi Metaphysics: Maintain specialized terms like fayd ("spiritual emanation"), barzakh ("spiritual isthmus"), and madad ("spiritual assistance") with clarifying conceptual annotations.
4. Wolof & Wolofal Layer: Render the Wolof translation in modern standardized Latin orthography. Where historical Ajami records exist, match the standard Wolof with its classic Wolofal equivalent. Account for "chanted syntheses" (such as the Apollo genre), where Arabic loanwords like taraqaa (to ascend) are artistically blended into Wolof syntactic frames.

Editorial Integrity & Scholarly Guardrails
- No Speculative Translations: If a passage is ambiguous, prioritize the authoritative translations and commentary published by the Sirâj Al-hadra Al-mâlikiyya de Tivaouane association.
- Historical Accuracy: Ensure notes align with the academic research standard of the Institut Fondamental d'Afrique Noire (IFAN) Cheikh Anta Diop.
- Tone: Maintain an elite, scholarly, highly respectful, and objective academic-theological tone.

Output must STRICTLY conform to the requested JSON format matching the schema properties provided. Do NOT output markdown code blocks or any other characters except raw JSON.
"""

    private const val FEW_SHOT_QUERY_1 = "Generate a synchronized dataset for Verse 1 of Taysir, Wassilatul muna."
    private const val FEW_SHOT_RESPONSE_1 = """{
  "work_metadata": {
    "title_arabic": "تيسير وسيلة المنى",
    "title_transliteration": "Taysīr Wasīlat al-Munā",
    "canonical_index_number": 1,
    "theological_category": "Liturgical Supplication & Divine Names"
  },
  "segmented_text": [
    {
      "verse_index": 1,
      "arabic_text": "بِسْمِ الإِلَهِ الْبَادِئِ الْمُحِيطِ الرَّحْمَنِ الرَّحِيمِ ذِي الْجَلَالِ النُّورِ",
      "latin_transliteration": "Bismi-llāhil-Bādi'il-Muḥīṭir-Raḥmānir-Raḥīmi Dhī-l-Jalālin-Nūr",
      "english_translation": "In the name of God, the Initiator, the Encompassing, the Merciful, the Compassionate, Possessor of Majesty and Light.",
      "wolof_translation_standard": "Ci turu Yàlla, Ku dore ka, Ku sàkkal mbindeef yépp, Boroom yërmënde ju yaatu ja, Boroom ndam ak leer.",
      "wolofal_ajami_script": "سِ تُرُ يَࣺلَّ كُ دُرِ كَ كُ سَࣺكَّلْ مْبِنْدِيفْ يِࣺپْ بُرُومْ نْدَمْ اَكْ لِيرْ",
      "memorization_loop_count": 3,
      "suggested_tasbih_target": 99
    }
  ],
  "oral_tradition_metadata": {
    "chanted_style_reference": "Tivaouane Traditional Hadra Recitation",
    "singing_melody_air_description": "Chanted in a slow, meditative, rhythmic cadence designed to induce spiritual focus during group dhikr.",
    "historical_context_notes": "Originally compiled in 1885, this opening stanza weaves the foundational Divine Names to establish protection and direct communion."
  }
}"""

    suspend fun queryArchiveEngine(userQuery: String, customApiKey: String? = null): ArchiveResponse? = withContext(Dispatchers.IO) {
        val finalApiKey = customApiKey ?: BuildConfig.GEMINI_API_KEY
        if (finalApiKey.isEmpty() || finalApiKey == "MY_GEMINI_API_KEY") {
            return@withContext null
        }

        val formattedPrompt = """
Generate structured scholarly output for the following query. Make sure to adhere to the EHMS-Engine guidelines and output valid JSON conforming to the requested schema.

Query: $userQuery

Expected JSON Schema Structure:
{
  "work_metadata": {
    "title_arabic": "string",
    "title_transliteration": "string",
    "canonical_index_number": integer,
    "theological_category": "string"
  },
  "segmented_text": [
    {
      "verse_index": integer,
      "arabic_text": "string",
      "latin_transliteration": "string",
      "english_translation": "string",
      "wolof_translation_standard": "string",
      "wolofal_ajami_script": "string (optional)",
      "memorization_loop_count": integer (optional, default 1),
      "suggested_tasbih_target": integer (optional)
    }
  ],
  "oral_tradition_metadata": {
    "chanted_style_reference": "string",
    "singing_melody_air_description": "string",
    "historical_context_notes": "string"
  }
}
"""

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = FEW_SHOT_QUERY_1))),
                GeminiContent(parts = listOf(GeminiPart(text = FEW_SHOT_RESPONSE_1))),
                GeminiContent(parts = listOf(GeminiPart(text = formattedPrompt)))
            ),
            generationConfig = GeminiGenerationConfig(
                temperature = 0.1f,
                responseMimeType = "application/json"
            ),
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = SYSTEM_INSTRUCTION)))
        )

        try {
            val response = RetrofitClient.service.generateContent(finalApiKey, request)
            val rawJson = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (rawJson != null) {
                // Parse the response
                val adapter = RetrofitClient.moshiParser.adapter(ArchiveResponse::class.java)
                adapter.fromJson(rawJson)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
