# Java-Servlet-Technology

### ✔️Servlet

* Servlet file: **src/main/java/ro/uaic/info/hello/helloworld/Lab1Servlet.java**
* **doPost** method writes data in the repository and/or returns content depending on the value of the provided parameters (various edge-cases were treated)
* via the **storeEntryInRepository** method, a line can be added to the repository file
* the **showRepositoryContentInHTMLFormat** method returns the repository content in HTML format, ordered by key
* The servlet invocation will be done using a simple HTML form (**src/main/webapp/index.jsp**)
* The logging is performed using the **writeRequestInformationInServerLog** method

### ✔️Invoke the service from a desktop application

* Python application: **service-invoker.py**
* If the user agent is not a browser, tht servlet will return the repository content in simple text format using **showRepositoryContentInSimpleText** method

### ❌ Analyze the performance and concurrency issues