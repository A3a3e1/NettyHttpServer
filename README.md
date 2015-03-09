# NettyHttpServer

Реализован  http-сервер на фреймворке netty версии 4.0.0.CR9.

1. Запрос http://127.0.0.1:8080/hello возвращает "Hello World" с 10-секундной задержкой. В ответ на запрос возвращается
html файл с текстом "Hello World" (также можно возвращать простой текст).

2. Запрос на http://127.0.0.1:8080/redirect?url=<url> переадресует на путь, указанный в =<url>
Реализовано при помощи отправки нового объекта Response (new DefaultFullHttpResponse) 
с соответствующим заголовком HttpHeaders.Names.LOCATION

3. По запросу на http://127.0.0.1:8080/status выдается статистика. Скриншоты приведены ниже.
Создана таблица на БД MySQL. При помощи коннектора mysql-connector-java-5.1.34 sql запросами делаются 
необходимые выборки из БД, добавляются новые записи и т.д.
За это отвечает отдельный класс StatusHandler. При помощи библиотеки RenderSnake 1.8 формируется html файл 
status.html и передается на вывод

4. Запрос на любую другую страницу вернет файл 404.html

5. Скриншоты
6. 1) - скриншоты как выглядят станицы /status в рабочем приложении:

![Alt text](/report/1_1_status_before.png?raw=true "Screenshot before 1")
![Alt text](/report/1_2_status_before.png?raw=true "Screenshot before 2")
![Alt text](/report/1_3_status_before.png?raw=true "Screenshot before 3")

2)  - скриншот результата выполнения команды ab – c 100 – n 10000 http://somedomain/status

Команду ab с указанными параметрами успешно выполнить не удалось. Предполагаю, что это связано с конфигурацией
домашнего ПК или неидеальной настройкой сервера на netty
Успешно была выполнена команда со следующими параметрами:

![Alt text](/report/status_ab.png?raw=true "Screenshot ab")

3)  - еще один скриншот станицы /status, но уже после выполнение команды ab из предыдущего пункта

![Alt text](/report/2_1_status_after.png?raw=true "Screenshot after 1")
![Alt text](/report/2_2_status_after.png?raw=true "Screenshot after 2")
![Alt text](/report/2_3_status_after.png?raw=true "Screenshot after 3")
