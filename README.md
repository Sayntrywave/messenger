# messenger

https://drive.google.com/drive/folders/1nnL2zV8XPLE8q6k6T67_VnpqVyotA7O9?usp=sharing - демонстрация

Я написал для удобства клиент на джаве, так что пользуйтесь :)

https://app.swaggerhub.com/apis-docs/SAYNTRYWAVE_1/messenger/1.0.0 - свагер

По критериям выполнил все(включая все дополнительные заданиия), кроме написания тестов

По поводу клиента: программа будет ожидать от вас логина и пароля на входе, чтобы провести авторизацию.

Авторизация реализована с помощью spring security(jwt). Токены передаются во всех методам в хедере "Authorization"кроме
этих эндпоинтов "/login", "/register", "/error", "/activate". Инвалидировать токен можно через /logout.

В /user/edit передается UserEditRequest в который можно передавать только тот параметр, который вы хотите изменить.

При изменении почты отправляется ссылку на нее с подтверждением, при нажатии почта поменяется
Тоже самои и для отключение аккаунта

