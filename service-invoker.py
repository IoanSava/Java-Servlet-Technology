import requests

mock = "false"
sync = "true"
key = "short-key"
value = 4

params = {
    "mock": mock,
    "sync": sync,
    "key": key,
    "value": value
}

response = requests.post('http://localhost:8080/HelloWorld-1.0/lab1', params)

if response.status_code == 200:
    print(response.text)
