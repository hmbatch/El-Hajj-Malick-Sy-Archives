package com.example

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class Work(
    val index: Int,
    val titleArabic: String,
    val titleTransliteration: String,
    val theologicalCategory: String,
    val description: String,
    val verses: List<Verse> = emptyList(),
    val oralTradition: OralTraditionMetadata = OralTraditionMetadata(),
    val pdfUrls: Map<String, String> = emptyMap()
)

@Keep
@JsonClass(generateAdapter = true)
data class Verse(
    val verseIndex: Int,
    val arabicText: String,
    val latinTransliteration: String,
    val englishTranslation: String,
    val wolofTranslationStandard: String,
    val wolofalAjamiScript: String = "",
    val memorizationLoopCount: Int = 1,
    val suggestedTasbihTarget: Int = 11
)

@Keep
@JsonClass(generateAdapter = true)
data class OralTraditionMetadata(
    val chantedStyleReference: String = "Tivaouane Traditional Hadra Recitation",
    val singingMelodyAirDescription: String = "Chanted in a slow, meditative, rhythmic cadence designed to induce spiritual focus during group dhikr.",
    val historicalContextNotes: String = "Translated and structured under the scholarly standards of the Sirâj Al-hadra association."
)

object CorpusData {
    val dummyVerses = listOf(
        Verse(
            verseIndex = 1,
            arabicText = "بِسْمِ الإِلَهِ الْبَادِئِ الْمُحِيطِ الرَّحْمَنِ الرَّحِيمِ ذِي الْجَلَالِ النُّورِ",
            latinTransliteration = "Bismi-llāhil-Bādi'il-Muḥīṭir-Raḥmānir-Raḥīmi Dhī-l-Jalālin-Nūr",
            englishTranslation = "In the name of God, the Initiator, the Encompassing, the Merciful, the Compassionate, Possessor of Majesty and Light.",
            wolofTranslationStandard = "Ci turu Yàlla, Ku dore ka, Ku sàkkal mbindeef yépp, Boroom yërmënde ju yaatu ja, Boroom ndam ak leer.",
            wolofalAjamiScript = "سِ تُرُ يَࣺلَّ كُ دُرِ كَ كُ سَࣺكَّلْ مْبِنْدِيفْ يِࣺپْ بُرُومْ نْدَمْ اَكْ لِيرْ",
            memorizationLoopCount = 3,
            suggestedTasbihTarget = 99
        ),
        Verse(
            verseIndex = 2,
            arabicText = "الْحَمْدُ لِلَّهِ الَّذِي هَدَانَا بِالْأَسْمَاءِ الْحُسْنَى وَاصْطَفَانَا بِالْهُدَى",
            latinTransliteration = "Alḥamdulillāhil-ladhī hadānā bil-Asmā'il-Ḥusnā wa-ṣṭafānā bil-hudā",
            englishTranslation = "Praise be to God who guided us through the Beautiful Names and selected us for guidance.",
            wolofTranslationStandard = "Sant mën na Yàlla mi nu gindi mënnu mënnu ci Turam yu rafet yi, te tànn nu ci njub.",
            wolofalAjamiScript = "سَنْتْ مِࣺنْ نَ يَࣺلَّ مِ نُ جِنْدِ مِࣺنُّ مِࣺنُّ سِ تُرَمْ يُ رَفِتْ يِ تِ تَنُّ نُ سِ نْجُبْ",
            memorizationLoopCount = 2,
            suggestedTasbihTarget = 33
        ),
        Verse(
            verseIndex = 3,
            arabicText = "نَدْعُوكَ يَا اللَّهُ يَا رَحْمَنُ يَا رَحِيمُ أَنْ تَغْفِرَ ذُنُوبَنَا وَتَشْفِي صُدُورَنَا",
            latinTransliteration = "Nad‘ūka yā Allāhu yā Raḥmānu yā Raḥīmu an taghfira dhunūbanā wa tashfiya ṣudūranā",
            englishTranslation = "We call upon You, O Allah, O Merciful, O Compassionate, to forgive our sins and heal our hearts.",
            wolofTranslationStandard = "Noongi lay woo yā Yàlla, Ku yërménde, Ku saafara, ngir nga baal sunuy bàkkar te fadd sunuy dën.",
            wolofalAjamiScript = "نُونْجِ لَيْ وُ يَا يَࣺلَّ كُ يِࣺرْمِينْدِ كُ سَافَرَ نْجِرْ نْجَ بَالْ سُنُيْ بَࣺكَّرْ تِ فَدْ سُنُيْ دِࣺنْ",
            memorizationLoopCount = 4,
            suggestedTasbihTarget = 100
        ),
        Verse(
            verseIndex = 4,
            arabicText = "رَبَّنَا تَقَبَّلْ مِنَّا صَلَاتَنَا وَدُعَاءَنَا وَقِيَامَنَا فِي الدُّجَى",
            latinTransliteration = "Rabbana taqabbal minnā ṣalātanā wa du‘ā’anā wa qiyāmanā fid-dujā",
            englishTranslation = "Our Lord, accept from us our prayers, our supplications, and our standing in devotion in the darkness.",
            wolofTranslationStandard = "Sunu Boroom, nangu lëll sunuy ñaan, sunuy sàll, ak sunu taxaw ci guddi gu lëndëm gi.",
            wolofalAjamiScript = "سُنُ بُرُومْ نَنْجُ لِࣺلْ سُنُيْ نْيَانْ سُنُيْ سَلْ اَكْ سُنُ تَخَوْ سِ جُدِّ جُ لِࣺنْدِࣺمْ جِ",
            memorizationLoopCount = 3,
            suggestedTasbihTarget = 33
        ),
        Verse(
            verseIndex = 5,
            arabicText = "وَاهْدِنَا صِرَاطَ الْمُسْتَقِيمِ صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ",
            latinTransliteration = "Wahdinā ṣirāṭal-mustaqīmi ṣirāṭal-ladhīna an‘amta ‘alayhim",
            englishTranslation = "And guide us to the straight path, the path of those upon whom You have bestowed Your favors.",
            wolofTranslationStandard = "Te gindi nu yoon wu jub wa, yoonu ñi nga barkeel.",
            wolofalAjamiScript = "تِ جِنْدِ نُ يُونْ وُ جُبْ وَ يُونُ نْيِ نْجَ بَرْكِيلْ",
            memorizationLoopCount = 2,
            suggestedTasbihTarget = 66
        ),
        Verse(
            verseIndex = 6,
            arabicText = "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ نَسْتَغِيثُ أَصْلِحْ لَنَا شَأْنَنَا كُلَّهُ",
            latinTransliteration = "Yā Ḥayyu yā Qayyūmu bi-raḥmatika nastaghīthu aṣliḥ lanā sha'nanā kullahu",
            englishTranslation = "O Ever-Living, O Self-Sustaining, by Your mercy we seek aid; rectify for us all of our affairs.",
            wolofTranslationStandard = "Yaw mi dund mënnu te taxawal mbir yépp, ci sa yërménde noongi lay ñaan nga dëgëral sunuy dund yépp.",
            wolofalAjamiScript = "يَوْ مِ دُنْدْ مِࣺنُّ تِ تَخَوَلْ مْبِرْ يِࣺپْ سِ سَ يِࣺرْمِينْدِ نُونْجِ لَيْ نْيَانْ نْجَ دِࣺجِࣺرَلْ سُنُيْ دُنْدْ يِࣺپْ",
            memorizationLoopCount = 5,
            suggestedTasbihTarget = 99
        )
    )

    val works = listOf(
        Work(
            index = 1,
            titleArabic = "تيسير وسيلة المنى",
            titleTransliteration = "Taysīr Wasīlat al-Munā",
            theologicalCategory = "Liturgical Supplication & Divine Names",
            description = "The celebrated poetic prayer woven around the 99 Divine Names (Asma al-Husna). Used daily for protection, spiritual alignment, and supplication.",
            verses = dummyVerses,
            oralTradition = OralTraditionMetadata(
                chantedStyleReference = "Tivaouane Traditional Hadra Recitation",
                singingMelodyAirDescription = "Chanted in a slow, meditative, rhythmic cadence designed to induce spiritual focus during group dhikr.",
                historicalContextNotes = "Originally compiled in 1885, this opening stanza weaves the foundational Divine Names to establish protection and direct communion."
            ),
            pdfUrls = mapOf("Original Manuscript" to "https://sopnabyfrance.com/wp-content/uploads/2024/03/TAISSIR-DAHIRA-SOP-NABY-FRANCE.pdf")
        ),
        Work(
            index = 2,
            titleArabic = "إفحام المنكر الجاني",
            titleTransliteration = "Ifḥām al-Munkir al-Jānī",
            theologicalCategory = "Defensive Jurisprudence & Apologia",
            description = "The major scholarly apologia defending the orthodox jurisprudential and theological foundations of the Tijaniyya Sufi order.",
            verses = dummyVerses,
            oralTradition = OralTraditionMetadata(
                chantedStyleReference = "Scholarly Prose Recitation",
                singingMelodyAirDescription = "Delivered in serious, authoritative, didactic tones without excessive chanting to preserve legalistic clarity.",
                historicalContextNotes = "Written as a highly rigorous defense against literalist critiques, demonstrating deep adherence to orthodox Sunni Malikite standards."
            ),
            pdfUrls = mapOf("Original Manuscript" to "https://sopnabyfrance.com/wp-content/uploads/2024/03/Ifham-al-Munkir-al-Jani.pdf")
        ),
        Work(
            index = 3,
            titleArabic = "خلاص الذهب",
            titleTransliteration = "Khilāṣu-dh-Dhahab",
            theologicalCategory = "Sīra (Prophetic Biography) & Poetry",
            description = "The definitive rhyming poetic biography (Sira) of the Holy Prophet Muhammad, celebrated for its extraordinary poetic elegance and deep devotion.",
            verses = dummyVerses,
            oralTradition = OralTraditionMetadata(
                chantedStyleReference = "Chanted Ode of the Mawlid (Gamou)",
                singingMelodyAirDescription = "Sung in highly emotional, celebratory, and rhythmic melodies, driving communal elevation during the Mawlid gatherings.",
                historicalContextNotes = "Recited worldwide during the annual Gamou (Mawlid) festival in Senegal, particularly in Tivaouane."
            ),
            pdfUrls = mapOf(
                "Original Manuscript" to "https://sopnabyfrance.com/wp-content/uploads/2024/03/KHILASS-خلاص-الذهب.pdf",
                "French Translation" to "https://sopnabyfrance.com/wp-content/uploads/2024/03/Khilasu-Thahab-traduit-.pdf"
            )
        ),
        Work(
            index = 4,
            titleArabic = "شرح خلاص الذهب",
            titleTransliteration = "Sharḥ Khilāṣi-dh-Dhahab",
            theologicalCategory = "Historical-Theological Prose Commentary",
            description = "The author's own detailed prose commentary explaining the historical-theological context of the verses in Khilaçu ez-Zahab.",
            verses = dummyVerses
        ),
        Work(
            index = 5,
            titleArabic = "زجر القلوب",
            titleTransliteration = "Zajr ul-Qulūb",
            theologicalCategory = "Asceticism & Spiritual Purification",
            description = "A collection of highly expressive, hortatory poetry focused on spiritual purification, self-accounting, and asceticism (Zuhd).",
            verses = dummyVerses,
            pdfUrls = mapOf("Original Manuscript" to "https://sopnabyfrance.com/wp-content/uploads/2024/03/Zajrul-qhulub-Arabe.pdf")
        ),
        Work(
            index = 6,
            titleArabic = "آداب المسجد",
            titleTransliteration = "Adab ul-Masjid",
            theologicalCategory = "Liturgical Rules & Etiquette",
            description = "A practical liturgical poem detailing the precise rules of conduct, spiritual focus, and ethical etiquette inside the mosque.",
            verses = dummyVerses,
            pdfUrls = mapOf("Original Manuscript" to "https://sopnabyfrance.com/wp-content/uploads/2024/03/Adabul-Masjid.pdf")
        ),
        Work(
            index = 7,
            titleArabic = "الهداية الولدان",
            titleTransliteration = "Al-Hidāyat ul-Wildān",
            theologicalCategory = "Theological Creed (Aqidah)",
            description = "A dense, structured theological treatise teaching Islamic creed and scholastic theology according to orthodox Ash'arite formulations.",
            verses = dummyVerses
        ),
        Work(
            index = 8,
            titleArabic = "فاكهة الطلاب",
            titleTransliteration = "Fākihas al-Tullāb",
            theologicalCategory = "Instructional Sufi Manual & Ethics",
            description = "A foundational practical manual explaining the litanies, rituals, and ethical behavior required in the Tijaniyya spiritual path.",
            verses = dummyVerses,
            oralTradition = OralTraditionMetadata(
                chantedStyleReference = "Academic-Didactic Tafseer Mode",
                singingMelodyAirDescription = "Spoken or minimally chanted in instructional tones to preserve legalistic and spiritual clarity.",
                historicalContextNotes = "Translated under the scholarly standards of the Sirâj Al-hadra association, matching the classical French translation work of Prof. Rawane Mbaye."
            ),
            pdfUrls = mapOf(
                "Original Manuscript" to "https://sopnabyfrance.com/wp-content/uploads/2024/03/Faqihatou-toulab-1.pdf",
                "French Translation" to "https://sopnabyfrance.com/wp-content/uploads/2024/03/Fakihatou-Toulab-en-Francais.pdf"
            )
        ),
        Work(
            index = 9,
            titleArabic = "خطبة الجمعة",
            titleTransliteration = "Khutbātul Jumu'ah",
            theologicalCategory = "Liturgical Sermons & Oratory",
            description = "Formal Arabic sermon outlines composed for the Friday congregational prayers, rich in theological and moral guidance.",
            verses = dummyVerses,
            pdfUrls = mapOf("Original Manuscript" to "https://sopnabyfrance.com/wp-content/uploads/2024/03/Khutbatul-Jumua.pdf")
        ),
        Work(
            index = 10,
            titleArabic = "خطبة العيد",
            titleTransliteration = "Khutbātul 'Īd",
            theologicalCategory = "Liturgical Festive Sermons",
            description = "Formal sermon outlines written for the Eid al-Fitr and Eid al-Adha community celebrations.",
            verses = dummyVerses
        ),
        Work(
            index = 11,
            titleArabic = "كفاية الراغبين",
            titleTransliteration = "Kifāyat ar-Rāghibīn",
            theologicalCategory = "Malikite Jurisprudence & Counsel",
            description = "A detailed, masterfully written prose work compiling Malikite jurisprudential rulings and spiritual-ethical advice for the community.",
            verses = dummyVerses,
            pdfUrls = mapOf("Original Manuscript" to "https://sopnabyfrance.com/wp-content/uploads/2024/03/KIFAYATOUR-RAGHIBINE.pdf")
        ),
        Work(
            index = 12,
            titleArabic = "رسالة لطيفة",
            titleTransliteration = "Risālatun Laṭīfah",
            theologicalCategory = "Personal Ethics & Morality Epistles",
            description = "A short, accessible epistle conveying critical guidance on personal ethics, civic duty, and social morality.",
            verses = dummyVerses
        ),
        Work(
            index = 13,
            titleArabic = "حروف صلاة الفاتح",
            titleTransliteration = "Ḥurūfu Ṣalāt il-Fātiḥ",
            theologicalCategory = "Esoteric Metaphysics & Gnosis",
            description = "An esoteric, deeply spiritual study of the structural and metaphysical letters of the Salat al-Fatih, the core litany of light.",
            verses = dummyVerses
        ),
        Work(
            index = 14,
            titleArabic = "مجموعة دعاء الوظيفة",
            titleTransliteration = "Majmū‘atu Du‘ā’ il-Wazīfah",
            theologicalCategory = "Daily Liturgical Supplications",
            description = "The highly organized manual of supplications recited collectively during the daily Wazifa morning and evening circles.",
            verses = dummyVerses
        ),
        Work(
            index = 15,
            titleArabic = "قصيدة ري الزمان",
            titleTransliteration = "Khaṣīdatu Riyyi-z-Zamān (Nunya)",
            theologicalCategory = "Social Critique & Moral Reform",
            description = "A classical poetic masterpiece rhyming in the letter 'Nun', containing powerful social critiques and moral solutions for community reformation.",
            verses = dummyVerses
        ),
        Work(
            index = 16,
            titleArabic = "دعاء رفعات",
            titleTransliteration = "Du‘ā’u Ruf‘āt",
            theologicalCategory = "Liturgical Supplications for Protection",
            description = "A specific liturgical supplication written for spiritual elevation, preservation, and divine protection against hardships.",
            verses = dummyVerses,
            pdfUrls = mapOf(
                "Original Manuscript" to "https://sopnabyfrance.com/wp-content/uploads/2024/03/Doua-ou-Roufate.pdf",
                "Translation" to "https://sopnabyfrance.com/wp-content/uploads/2024/03/Laqhad-Roufi-ate.pdf"
            )
        ),
        Work(
            index = 17,
            titleArabic = "وسيلة المحاربين",
            titleTransliteration = "Wasīlat ul-Muḥārabīn",
            theologicalCategory = "Devotional Defenses & Shield",
            description = "A highly defensive devotional poem designed as a spiritual shield for physical, ethical, and metaphysical protection.",
            verses = dummyVerses
        )
    )

    fun getWorkByIndex(index: Int): Work? {
        return works.find { it.index == index }
    }

    fun getAuthenticVerseCount(index: Int): Int {
        return when (index) {
            1 -> 105 // Taysīr Wasīlat al-Munā
            2 -> 120 // Ifḥām al-Munkir al-Jānī
            3 -> 278 // Khilāṣu-dh-Dhahab
            4 -> 278 // Sharḥ Khilāṣi-dh-Dhahab
            5 -> 110 // Zajr ul-Qulūb
            6 -> 60  // Adab ul-Masjid
            7 -> 85  // Al-Hidāyat ul-Wildān
            8 -> 144 // Fākihas al-Tullāb
            9 -> 52  // Khutbātul Jumu'ah
            10 -> 12 // Khutbātul 'Īd
            11 -> 130 // Kifāyat ar-Rāghibīn
            12 -> 40 // Risālatun Laṭīfah
            13 -> 72 // Ḥurūfu Ṣalāt il-Fātiḥ
            14 -> 50 // Majmū‘atu Du‘ā’ il-Wazīfah
            15 -> 160 // Khaṣīdatu Riyyi-z-Zamān (Nunya)
            16 -> 35 // Du‘ā’u Ruf‘āt
            17 -> 95 // Wasīlat ul-Muḥārabīn
            else -> 45
        }
    }
}
