<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>My first JSP</title>
    <style>
        div {
            border-radius: 5px;
            background-color: #f2f2f2;
            padding: 20px;
            justify-content: center;
        }

        input {
            margin-bottom: 10px;
        }
    </style>
</head>
<body>
<h3>Invoke Lab1Servlet</h3>
<div>
    <form method="POST" action="lab1">
        <label for="key">Key</label><br>
        <input type="text" id="key" name="key"/><br>

        <label for="value">Value</label><br>
        <input type="number" id="value" name="value" min="0" value="1" required><br>

        <span>Mock</span>
        <input type="radio" id="mock-true" name="mock" value="true" checked>
        <label for="mock-true">true</label>
        <input type="radio" id="mock-false" name="mock" value="false">
        <label for="mock-false">false</label><br>

        <span>Sync</span>
        <input type="radio" id="sync-true" name="sync" value="true" checked>
        <label for="sync-true">true</label>
        <input type="radio" id="sync-false" name="sync" value="false">
        <label for="sync-false">false</label><br>
        <input type="submit" value="Invoke the servlet"/>
    </form>
</div>
</body>
</html>