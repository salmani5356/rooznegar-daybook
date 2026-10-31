روزنگار | بسته یکپارچه وب و اندروید

محتویات:
- index.html: برنامه وب فعلی با ورود ایمیلی و Supabase
- manifest.webmanifest و service-worker.js: نصب‌پذیری PWA و کش فایل‌های اصلی
- assets/: تصاویر و آیکون‌های اولیه
- android/: پروژه Android WebView که همان نسخه وب میزبانی‌شده را باز می‌کند
- .github/workflows/android.yml: ساخت خودکار APK آزمایشی از GitHub Actions

نحوه قرار دادن در مخزن:
1) از مخزن فعلی GitHub یک نسخه پشتیبان بگیر.
2) فایل ZIP را باز کن.
3) همه محتویات داخل ZIP را در ریشه مخزن rooznegar-daybook بارگذاری کن و Commit کن.
4) GitHub Pages باید از شاخه main و پوشه ریشه منتشر شود.
5) پس از Commit، از زبانه Actions، کار Build Rooznegar Android را اجرا کن.
6) APK آزمایشی را از بخش Artifacts همان اجرای workflow دریافت کن.

تنظیمات Supabase مورد استفاده در index.html:
Project URL و Publishable key عمومی در کد قرار دارند. این کلید برای مرورگر/اپ عمومی است.
هرگز service_role key یا رمز دیتابیس را در فایل‌ها قرار نده.

نکات مهم پیش از انتشار:
- این Android WebView فعلاً APK آزمایشی Debug می‌سازد، نه نسخه Release امضاشده فروشگاهی.
- برای انتشار رسمی باید keystore اختصاصی ایجاد و امضای release تنظیم شود؛ کلید را امن نگه‌دار و در GitHub قرار نده.
- در Supabase > Authentication > URL Configuration، Site URL و Redirect URL باید آدرس GitHub Pages را مجاز کنند.
- در جدول‌ها باید RLS و policyهای محدود به auth.uid() فعال باشند.
- منطق فعلی همگام‌سازی در این index.html از الگوی موجود شما استفاده می‌کند؛ پیش از انتشار عمومی، همگام‌سازی همزمان چند دستگاه و تعارض و حذف داده‌ها باید آزموده شود.
- Android wrapper لود می‌کند: https://salmani5356.github.io/rooznegar-daybook/ . پس نسخه وب باید اول با همین فایل منتشر شود.
- PWA و اپ Android یک وب‌سایت مشترک را نمایش می‌دهند، اما داده‌های localStorage داخل WebView و Chrome از هم جدا هستند؛ داده‌های مشترک بین دستگاه‌ها از مسیر Supabase و پس از ورود با همان ایمیل فراهم می‌شود.
- فایل privacy policy و فرم‌های محتوایی موردنیاز فروشگاه‌ها باید پیش از ارسال نهایی تکمیل شوند.
