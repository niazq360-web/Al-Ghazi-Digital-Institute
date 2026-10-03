package com.example.ui.localization

enum class AppLanguage(val code: String, val displayName: String, val isRtl: Boolean) {
    ENGLISH("en", "English", false),
    URDU("ur", "اردو", true),
    SINDHI("sd", "سنڌي", true)
}

object AppStrings {
    private val en = mapOf(
        "app_title" to "Al Ghazi Digital Institute",
        "tagline" to "LEARN • PRACTICE • GROW",
        "dashboard" to "Dashboard",
        "students" to "Students",
        "courses" to "Courses",
        "fees" to "Fee Management",
        "attendance" to "Attendance",
        "teachers" to "Teachers & Salary",
        "expenses" to "Expenses",
        "certificates" to "Certificates",
        "reports" to "Reports",
        "settings" to "Settings",
        "total_students" to "Total Students",
        "active_students" to "Active Students",
        "total_courses" to "Active Courses",
        "fee_collected" to "Fee Collected",
        "pending_fee" to "Pending Fee",
        "total_expenses" to "Total Expenses",
        "net_balance" to "Net Balance",
        "add_student" to "Add Student",
        "collect_fee" to "Collect Fee",
        "add_course" to "Add Course",
        "add_teacher" to "Add Teacher",
        "add_expense" to "Add Expense",
        "search_hint" to "Search by name, roll no, phone...",
        "print_receipt" to "Print Receipt",
        "download_pdf" to "Download PDF",
        "send_sms" to "Send SMS",
        "share" to "Share",
        "status" to "Status",
        "actions" to "Actions",
        "save" to "Save",
        "cancel" to "Cancel",
        "delete" to "Delete",
        "sync_cloud" to "Cloud Sync",
        "backup_restore" to "Backup & Restore",
        "session_notice" to "12-Month Academic Archive Active"
    )

    private val ur = mapOf(
        "app_title" to "الغازي ڈیجیٹل انسٹیٹیوٹ",
        "tagline" to "سیکھیں • مشق کریں • ترقی کریں",
        "dashboard" to "ڈیش بورڈ",
        "students" to "طلباء",
        "courses" to "کورسز",
        "fees" to "فیس مینیجمنٹ",
        "attendance" to "حاضری",
        "teachers" to "اساتذہ اور تنخواہ",
        "expenses" to "اخراجات",
        "certificates" to "سرٹیفکیٹس",
        "reports" to "رپورٹس",
        "settings" to "ترتیبات",
        "total_students" to "کل طلباء",
        "active_students" to "فعال طلباء",
        "total_courses" to "کورسز",
        "fee_collected" to "وصول شدہ فیس",
        "pending_fee" to "بقایا فیس",
        "total_expenses" to "کل اخراجات",
        "net_balance" to "خالص بچت",
        "add_student" to "نیا طالب علم",
        "collect_fee" to "فیس وصول کریں",
        "add_course" to "نیا کورس",
        "add_teacher" to "نیا استاد",
        "add_expense" to "نیا خرچ",
        "search_hint" to "نام، رول نمبر، فون سے تلاش کریں...",
        "print_receipt" to "رسید پرنٹ کریں",
        "download_pdf" to "پی ڈی ایف ڈاؤن لوڈ",
        "send_sms" to "ایس ایم ایس بھیجیں",
        "share" to "شیئر کریں",
        "status" to "حالت",
        "actions" to "اقدامات",
        "save" to "محفوظ کریں",
        "cancel" to "منسوخ",
        "delete" to "حذف کریں",
        "sync_cloud" to "کلاؤڈ سنک",
        "backup_restore" to "بیک اپ اور بحالی",
        "session_notice" to "12 ماہ کا تعلیمی ریکارڈ محفوظ ہے"
    )

    private val sd = mapOf(
        "app_title" to "الغازي ڊجيٽل انسٽيٽيوٽ",
        "tagline" to "سکيو • مشق ڪريو • اڳتي وڌو",
        "dashboard" to "ڊيش بورڊ",
        "students" to "شاگرد",
        "courses" to "ڪورسز",
        "fees" to "فيس مئنيجمينٽ",
        "attendance" to "حاضري",
        "teachers" to "استاد ۽ پگهار",
        "expenses" to "خرچ",
        "certificates" to "سرٽيفڪيٽس",
        "reports" to "رپورٽون",
        "settings" to "سيٽنگس",
        "total_students" to "ڪل شاگرد",
        "active_students" to "فعال شاگرد",
        "total_courses" to "ڪورس",
        "fee_collected" to "گڏ ٿيل فيس",
        "pending_fee" to "رهيل فيس",
        "total_expenses" to "ڪل خرچ",
        "net_balance" to "خالص بچت",
        "add_student" to "شاگرد داخل ڪريو",
        "collect_fee" to "فيس وصول ڪريو",
        "add_course" to "نئون ڪورس",
        "add_teacher" to "نئون استاد",
        "add_expense" to "نئون خرچ",
        "search_hint" to "نالو، رول نمبر، فون مان ڳوليو...",
        "print_receipt" to "رسيد پرنٽ ڪريو",
        "download_pdf" to "پي ڊي ايف ڊائون لوڊ",
        "send_sms" to "ايس ايم ايس موڪليو",
        "share" to "شيئر ڪريو",
        "status" to "حالت",
        "actions" to "ڪارروائي",
        "save" to "محفوظ ڪريو",
        "cancel" to "رد ڪريو",
        "delete" to "ختم ڪريو",
        "sync_cloud" to "ڪلائوڊ سنڪ",
        "backup_restore" to "بيڪ اپ ۽ بحالي",
        "session_notice" to "12 مهينن جو رڪارڊ محفوظ آهي"
    )

    fun get(key: String, languageCode: String): String {
        val map = when (languageCode) {
            "ur" -> ur
            "sd" -> sd
            else -> en
        }
        return map[key] ?: en[key] ?: key
    }
}
