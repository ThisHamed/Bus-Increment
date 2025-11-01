# Bus-Increment شارژینو
A specialized Android app for managing bus fares by reading/writing to MiFare cards, featuring direct SQL server sync for real-time data, payment processing, and receipt printing, all built on a modern MVVM/UDF architecture with Jetpack Compose.

Bus Increasement is a specialized Android application designed for managing public transportation bus fares. The app interacts directly with MiFare bus cards, providing a complete solution for reading, writing, and updating card balances. It also integrates payment and receipt printing functionalities, making it a comprehensive tool for transit operators.Built on a modern Android architecture, the application leverages MVVM (Model-View-ViewModel) to ensure a clean separation of concerns and a scalable codebase. It also incorporates principles of UDF (Unidirectional Data Flow), which results in a predictable and easily debuggable state management system. This is evident in the ViewModel, where UI events are processed to update the state, which the UI then observes.The core of the app's backend communication relies on a direct SQL Server connection to synchronize data, manage transactions, and validate card information in real-time. For local data persistence, such as storing server configurations, the app uses the Room database library.

Key Technical Features:
•Architecture: The project is architected using MVVM and UDF principles, promoting a reactive and maintainable structure.
•Dependency Injection: Hilt is used for managing dependencies, simplifying the process of providing objects like repositories to ViewModels.
•Asynchronous Operations: The app makes extensive use of Kotlin Coroutines and Flow for handling asynchronous tasks, such as network requests to the SQL server and database operations. This ensures the UI remains responsive and non-blocking.
•UI: The user interface is built with Jetpack Compose, a modern declarative UI toolkit for creating beautiful and responsive layouts.
•Hardware Interaction: The application is designed to interface with hardware for:
◦Reading and writing to MiFare bus cards.
◦Processing payments.
◦Connecting to a printer for receipts.
•Database Management:
◦Room: Used for on-device storage of critical data like server configurations.
◦JDBC (jtds): Manages the direct connection to a remote SQL Server for live data operations, which is a key part of the app's functionality.

Card Management: Read the current balance and data from a MiFare bus card.
•Fare Handling: Write new values to the card after a payment or "increment."
•Payment Processing: Integrated payment workflows.
•Receipt Printing: Generates and prints receipts for transactions.
•Server Configuration: Allows users to configure and test the connection to the backend SQL server, with real-time feedback on the connection status.

#شارژینو
یک اپلیکیشن تخصصی اندروید برای مدیریت کرایه‌های اتوبوس از طریق خواندن/نوشتن روی کارت‌های MiFare، با قابلیت همگام‌سازی مستقیم با سرور SQL برای داده‌های لحظه‌ای، پردازش پرداخت و چاپ رسید، ساخته‌شده بر پایه معماری مدرن MVVM/UDF با استفاده از Jetpack Compose.

شارژینو یک اپلیکیشن اندرویدی تخصصی است که برای مدیریت کرایه‌های حمل‌ونقل عمومی طراحی شده است. این برنامه به‌طور مستقیم با کارت‌های اتوبوس MiFare تعامل دارد و راهکاری کامل برای خواندن، نوشتن و به‌روزرسانی موجودی کارت ارائه می‌دهد. همچنین قابلیت‌های پرداخت و چاپ رسید را نیز در خود جای داده و به ابزاری جامع برای اپراتورهای حمل‌ونقل تبدیل شده است.

این اپلیکیشن بر پایه معماری مدرن اندروید توسعه یافته و از الگوی MVVM برای جداسازی منطقی بخش‌ها و ایجاد کدی مقیاس‌پذیر بهره می‌برد. همچنین اصول UDF (جریان داده یک‌طرفه) در آن پیاده‌سازی شده که منجر به مدیریت وضعیت قابل پیش‌بینی و آسان برای دیباگ می‌شود. این موضوع در ViewModel مشهود است، جایی که رویدادهای UI پردازش شده و وضعیت به‌روزرسانی می‌شود و UI آن را مشاهده می‌کند.
هسته ارتباطات بک‌اند اپلیکیشن بر پایه اتصال مستقیم به SQL Server بنا شده تا داده‌ها را همگام‌سازی کند، تراکنش‌ها را مدیریت کرده و اطلاعات کارت را به‌صورت لحظه‌ای اعتبارسنجی نماید. برای ذخیره‌سازی محلی داده‌ها مانند تنظیمات سرور، از کتابخانه Room استفاده شده است.

ویژگی‌های کلیدی فنی:
•معماری (Architecture): پروژه با استفاده از اصول MVVM و UDF معماری شده است که به ایجاد ساختاری واکنشی (Reactive) و قابل نگهداری کمک می‌کند.
•تزریق وابستگی (Dependency Injection): از Hilt برای مدیریت وابستگی‌ها استفاده شده که فرآیند ارائه نیازمندی‌ها (مانند Repository) به ViewModel‌ها را ساده می‌سازد.
•عملیات ناهمگام (Asynchronous Operations): اپلیکیشن به طور گسترده از Kotlin Coroutines و Flow برای مدیریت وظایف پس‌زمینه مانند درخواست‌های شبکه به سرور SQL و عملیات پایگاه داده استفاده می‌کند. این کار تضمین می‌کند که رابط کاربری همیشه پاسخگو باقی بماند.
•رابط کاربری (UI): رابط کاربری با Jetpack Compose ساخته شده است که یک ابزار مدرن و اعلانی (Declarative) برای ساخت لایه‌های کاربری زیبا و واکنش‌گرا است.
•تعامل با سخت‌افزار (Hardware Interaction): این اپلیکیشن برای تعامل با سخت‌افزارهای زیر طراحی شده است:
◦خواندن و نوشتن روی کارت‌های اتوبوس مایفر (MiFare).
◦پردازش پرداخت‌ها.
◦اتصال به چاپگر برای صدور رسید.
•مدیریت پایگاه داده (Database Management):
◦کتابخانه Room: برای ذخیره‌سازی داده‌های حیاتی مانند پیکربندی سرور بر روی دستگاه استفاده می‌شود.
◦کتابخانه JDBC (jtds): اتصال مستقیم به SQL Server را برای عملیات لحظه‌ای داده‌ها مدیریت می‌کند که بخش کلیدی عملکرد برنامه است.

عملکردهای اصلی:
•مدیریت کارت: خواندن موجودی و داده‌های فعلی از روی کارت اتوبوس مایفر.
•مدیریت کرایه: ثبت مقادیر جدید روی کارت پس از پرداخت یا «افزایش اعتبار».
•پردازش پرداخت: شامل جریان‌های کاری یکپارچه برای انجام پرداخت.
•چاپ رسید: تولید و چاپ رسید برای تراکنش‌ها.
•پیکربندی سرور: به کاربران اجازه می‌دهد تا اتصال به سرور SQL را پیکربندی و آزمایش کنند و بازخورد لحظه‌ای در مورد وضعیت اتصال دریافت نمایند.

