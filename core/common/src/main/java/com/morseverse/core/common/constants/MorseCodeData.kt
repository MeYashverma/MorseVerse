package com.morseverse.core.common.constants

/**
 * Complete International Morse Code data
 */
object MorseCodeData {

    val INTERNATIONAL_MORSE: Map<String, String> = mapOf(
        // Letters
        "A" to ".-",    "B" to "-...",  "C" to "-.-.",  "D" to "-..",
        "E" to ".",     "F" to "..-.",  "G" to "--.",   "H" to "....",
        "I" to "..",    "J" to ".---",  "K" to "-.-",   "L" to ".-..",
        "M" to "--",    "N" to "-.",    "O" to "---",   "P" to ".--.",
        "Q" to "--.-",  "R" to ".-.",   "S" to "...",   "T" to "-",
        "U" to "..-",   "V" to "...-",  "W" to ".--",   "X" to "-..-",
        "Y" to "-.--",  "Z" to "--..",

        // Numbers
        "0" to "-----", "1" to ".----", "2" to "..---", "3" to "...--",
        "4" to "....-", "5" to ".....", "6" to "-....", "7" to "--...",
        "8" to "---..", "9" to "----.",

        // Punctuation
        "." to ".-.-.-",  "," to "--..--",  "?" to "..--..",
        "'" to ".----.",  "!" to "-.-.--",  "/" to "-..-.",
        "(" to "-.--.",   ")" to "-.--.-",  "&" to ".-...",
        ":" to "---...",  ";" to "-.-.-.",  "=" to "-...-",
        "+" to ".-.-.",   "-" to "-....-",  "_" to "..--.-",
        "\"" to ".-..-.", "$" to "...-..-", "@" to ".--.-.",

        // Prosigns
        "<AS>" to ".-...",    "<BT>" to "-...-",   "<CT>" to "-.-.-",
        "<DO>" to "-..---",  "<KA>" to "-.-.-",   "<KN>" to "-.--.",
        "<SK>" to "...-.-",  "<SN>" to "...-.",   "<SOS>" to "...---...",
        "<HH>" to "........", "<VE>" to "...-.",   "<INT>" to "..-.-.",
        "<NR>" to "-..-.",   "<AA>" to ".-.-",    "<WA>" to ".--.-",
        "<IM>" to "..--",    "<VE2>" to "...-.-"
    )

    val REVERSE_MORSE: Map<String, String> = INTERNATIONAL_MORSE.entries
        .filter { !it.key.startsWith("<") || it.key == "<SOS>" }
        .associate { (k, v) -> v to k }

    val ALL_PROSIGNS: Map<String, String> = mapOf(
        "SOS" to "...---...",    "AS" to ".-...",    "BT" to "-...-",
        "CT" to "-.-.-",         "KA" to "-.-.-",   "KN" to "-.--.",
        "SK" to "...-.-",        "SN" to "...-.",   "HH" to "........",
        "VE" to "...-.",         "INT" to "..-.-.", "NR" to "-..-.",
        "AA" to ".-.-",          "WA" to ".--.-",   "IM" to "..--"
    )

    // Traditional learning groups (from simplest to most complex)
    val TRADITIONAL_GROUPS: List<List<String>> = listOf(
        listOf("E", "T"),
        listOf("I", "A"),
        listOf("N", "M"),
        listOf("S", "U"),
        listOf("R", "W"),
        listOf("D", "K"),
        listOf("G", "O"),
        listOf("H", "V"),
        listOf("F", "L"),
        listOf("P", "J"),
        listOf("B", "X"),
        listOf("C", "Y"),
        listOf("Z", "Q"),
        listOf("1", "2", "3", "4", "5"),
        listOf("6", "7", "8", "9", "0"),
        listOf(".", ",", "?", "/", "!")
    )

    // Koch method lesson sequence
    val KOCH_LESSONS: List<List<String>> = listOf(
        listOf("K", "M"),
        listOf("U", "R"),
        listOf("E", "S"),
        listOf("N", "A"),
        listOf("P", "T"),
        listOf("L", "W"),
        listOf("I", "."),
        listOf("J", "Z"),
        listOf("=", "F"),
        listOf("O", "Y"),
        listOf("V", ","),
        listOf("G", "5"),
        listOf("/","Q"),
        listOf("9", "2"),
        listOf("H", "3"),
        listOf("8", "B"),
        listOf("4", "7"),
        listOf("C", "1"),
        listOf("D", "6"),
        listOf("0", "X"),
        listOf("?", "-"),
        listOf("!", "@")
    )

    // Common words for practice
    val COMMON_WORDS: List<String> = listOf(
        "THE", "BE", "TO", "OF", "AND", "A", "IN", "THAT", "HAVE", "I",
        "IT", "FOR", "NOT", "ON", "WITH", "HE", "AS", "YOU", "DO", "AT",
        "THIS", "BUT", "HIS", "BY", "FROM", "THEY", "WE", "SAY", "HER", "SHE",
        "OR", "AN", "WILL", "MY", "ONE", "ALL", "WOULD", "THERE", "THEIR", "WHAT",
        "SO", "UP", "OUT", "IF", "ABOUT", "WHO", "GET", "WHICH", "GO", "ME",
        "CQ", "DE", "73", "88", "ES", "TNX", "FB", "OM", "YL", "RIG",
        "ANT", "PWR", "RPT", "SIG", "RST", "QTH", "QRM", "QRN", "QSB", "QSL",
        "CQDX", "DX", "DXPEDITION", "HAM", "ELMER", "RAGCHEW", "NET", "REPEATER",
        "SOTA", "POTA", "FIELD", "DAY", "CONTEST", "SPEED", "WPM", "CW"
    )

    // Common callsign prefixes
    val CALLSIGN_PREFIXES: List<String> = listOf(
        "W", "K", "N", "AA", "AB", "AC", "AD", "AE", "AF", "AG", "AI", "AJ", "AK", "AL",
        "VE", "VA", "VY", "G", "M", "2E", "F", "DL", "DJ", "DK", "DM", "DN",
        "I", "IK", "IU", "IV", "JA", "JH", "JI", "JJ", "JK", "JL", "JM", "JN", "JO", "JP",
        "VK", "ZL", "ZS", "PY", "LU", "CE", "CX", "UA", "RA", "RK", "RN", "RV", "RW", "RX",
        "OH", "SM", "LA", "OZ", "ON", "PA", "HB9", "HB0", "9A", "S5", "OK", "OM", "SP", "SQ"
    )

    // Q Codes (most common)
    val Q_CODES: List<Triple<String, String, String>> = listOf(
        Triple("QTH", "What is your location?", "My location is..."),
        Triple("QRM", "Are you being interfered with?", "I am being interfered with..."),
        Triple("QRN", "Are you troubled by static?", "I am troubled by static..."),
        Triple("QRS", "Shall I send slower?", "Send slower..."),
        Triple("QRT", "Shall I stop sending?", "Stop sending..."),
        Triple("QRU", "Have you anything for me?", "I have nothing for you."),
        Triple("QRV", "Are you ready?", "I am ready."),
        Triple("QRZ", "Who is calling me?", "You are being called by..."),
        Triple("QSB", "Are my signals fading?", "Your signals are fading."),
        Triple("QSL", "Can you acknowledge receipt?", "I acknowledge receipt."),
        Triple("QSO", "Can you communicate with...?", "I can communicate with..."),
        Triple("QSY", "Shall I change frequency?", "Change frequency to..."),
        Triple("QTH", "What is your location?", "My location is..."),
        Triple("QTR", "What is the correct time?", "The time is..."),
        Triple("QTX", "Will you keep your station open?", "I will keep my station open."),
        Triple("QBL", "Shall I move down the band?", "Move down the band."),
        Triple("QNB", "Shall I send each word twice?", "Send each word twice."),
        Triple("QND", "Are you operating in auto?", "I am operating in auto."),
        Triple("QNE", "What should I say to...?", "Say..."),
        Triple("QNF", "Are your signals fading?", "My signals are fading.")
    )

    // Phonetic alphabet
    val PHONETIC_ALPHABET: List<Triple<String, String, String>> = listOf(
        Triple("A", "Alpha", ".-"),
        Triple("B", "Bravo", "-..."),
        Triple("C", "Charlie", "-.-."),
        Triple("D", "Delta", "-.."),
        Triple("E", "Echo", "."),
        Triple("F", "Foxtrot", "..-."),
        Triple("G", "Golf", "--."),
        Triple("H", "Hotel", "...."),
        Triple("I", "India", ".."),
        Triple("J", "Juliet", ".---"),
        Triple("K", "Kilo", "-.-"),
        Triple("L", "Lima", ".-.."),
        Triple("M", "Mike", "--"),
        Triple("N", "November", "-."),
        Triple("O", "Oscar", "---"),
        Triple("P", "Papa", ".--."),
        Triple("Q", "Quebec", "--.-"),
        Triple("R", "Romeo", ".-."),
        Triple("S", "Sierra", "..."),
        Triple("T", "Tango", "-"),
        Triple("U", "Uniform", "..-"),
        Triple("V", "Victor", "...-"),
        Triple("W", "Whiskey", ".--"),
        Triple("X", "X-ray", "-..-"),
        Triple("Y", "Yankee", "-.--"),
        Triple("Z", "Zulu", "--..")
    )

    // Memory tips for characters
    val MEMORY_TIPS: Map<String, String> = mapOf(
        "A" to "Think of 'A' as a short dash following a dot — like saying 'ay!'",
        "B" to "One long dash followed by three dots — like 'BUM diddly'",
        "C" to "Dash-dot-dash-dot — like a heartbeat pattern",
        "D" to "One dash followed by two dots — 'DASH diddly'",
        "E" to "The simplest letter — just one dot!",
        "F" to "Dot-dot-dash-dot — 'did-did-DAH-dit'",
        "G" to "Two dashes then a dot — 'GO GO fast!'",
        "H" to "Four dots — 'hip-hip-hip-hip'",
        "I" to "Just two dots — 'it's easy!'",
        "J" to "Dot then three dashes — 'jump jump jump'",
        "K" to "Dash-dot-dash — 'KAN you see?'",
        "L" to "Dot-dash-dot-dot — 'let it be'",
        "M" to "Two dashes — 'mmm-mmm'",
        "N" to "One dash and one dot — 'no!'",
        "O" to "Three dashes — 'OH OH OH'",
        "P" to "Dot-dash-dash-dot — 'POPCORN'",
        "Q" to "Dash-dash-dot-dash — 'Q is Queen'",
        "R" to "Dot-dash-dot — 'ri-DOT'",
        "S" to "Three dots — 's-s-s' like a snake",
        "T" to "The second simplest — just one dash!",
        "U" to "Dot-dot-dash — 'you and me'",
        "V" to "Three dots then a dash — 'V for Victory'",
        "W" to "Dot-dash-dash — 'WOW'",
        "X" to "Dash-dot-dot-dash — 'eXit'",
        "Y" to "Dash-dot-dash-dash — 'YES YES'",
        "Z" to "Dash-dash-dot-dot — 'ZZ Top'",
        "0" to "Five dashes — all long",
        "1" to "One dot, four dashes — counting up!",
        "2" to "Two dots, three dashes",
        "3" to "Three dots, two dashes",
        "4" to "Four dots, one dash",
        "5" to "Five dots — all short",
        "6" to "One dash, four dots — reverse of 1",
        "7" to "Two dashes, three dots — reverse of 2",
        "8" to "Three dashes, two dots — reverse of 3",
        "9" to "Four dashes, one dot — reverse of 4"
    )

    // Story missions data
    data class StoryMissionData(
        val id: String,
        val title: String,
        val description: String,
        val characters: List<String>,
        val chapters: List<Pair<String, String>> // title, narrative
    )

    val STORY_MISSIONS: List<StoryMissionData> = listOf(
        StoryMissionData(
            id = "sos_rescue",
            title = "SOS Rescue",
            description = "Learn the most critical Morse signals to rescue a stranded sailor.",
            characters = listOf("S", "O", "E", "T", "M", "N"),
            chapters = listOf(
                "Chapter 1: The Signal" to "A faint signal crackles through the radio... SOS... SOS... You must learn to recognize the distress call!",
                "Chapter 2: Quick Response" to "You've detected the signal! Now learn S and O to decode the emergency message.",
                "Chapter 3: More Letters" to "The sailor is transmitting more. Learn E and T to understand the full message.",
                "Chapter 4: The Rescue" to "Decode the coordinates using M and N to complete the rescue mission!"
            )
        ),
        StoryMissionData(
            id = "spy_mission",
            title = "Spy Mission",
            description = "Intercept enemy communications as a Cold War spy.",
            characters = listOf("A", "I", "N", "R", "W", "D"),
            chapters = listOf(
                "Chapter 1: The Briefing" to "Agent, you've been selected for a top-secret mission. Learn A and I to begin intercepting messages.",
                "Chapter 2: First Intercept" to "Your first message comes through. Decode it using N and R.",
                "Chapter 3: Double Agent" to "A double agent is sending coded warnings. Use W and D to decode them.",
                "Chapter 4: Extraction" to "Decode the extraction coordinates to escape before it's too late!"
            )
        ),
        StoryMissionData(
            id = "space_mission",
            title = "Space Mission",
            description = "Communicate with a stranded astronaut using Morse light signals.",
            characters = listOf("H", "L", "P", "B", "V", "K"),
            chapters = listOf(
                "Chapter 1: Lost in Space" to "An astronaut's radio is damaged. They're flashing Morse with a flashlight! Learn H and L.",
                "Chapter 2: The Message" to "Decode the astronaut's first message using P and B.",
                "Chapter 3: Rescue Plan" to "Coordinate the rescue by learning V and K.",
                "Chapter 4: Homecoming" to "Guide the astronaut home with precise Morse communication!"
            )
        )
    )
}
