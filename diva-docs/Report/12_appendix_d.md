Server (TypeScript) Documentation
=================================

Index
-----

### External modules

-   ["server/App"](#sec:external-module-serverapp)
-   ["server/csv/CSVController"](#sec:external-module-servercsvcsvcontroller)
-   ["server/csv/index"](#sec:external-module-servercsvindex)
-   ["server/csv/routes"](#sec:external-module-servercsvroutes)
-   ["server/index"](#sec:external-module-serverindex)

------------------------------------------------------------------------

External module: "server/App"
-----------------------------

### Index

#### Classes

-   App

Class: App
----------

Configure the server application.

------------------------------------------------------------------------

### Constructors

#### **new App**(): App

*Defined in server/App.ts:12*

**Returns:** App

------------------------------------------------------------------------

### Properties

#### express

-   **express**: *`express.Application`*

*Defined in server/App.ts:12*

------------------------------------------------------------------------

### Methods

#### &lt;&lt;Private&gt;&gt; middleware

-   **middleware**(): `void`

    *Defined in server/App.ts:28*

    Configure Express middleware.

    **Returns:** `void`

------------------------------------------------------------------------

#### &lt;&lt;Private&gt;&gt; routes

-   **routes**(): `void`

    *Defined in server/App.ts:39*

    Configure API endpoints.

    **Returns:** `void`

------------------------------------------------------------------------

External module: "server/csv/CSVController"
-------------------------------------------

### Index

#### Classes

-   [CSVController](#sec:class-csvcontroller)

#### Variables

-   [exec](#sec:exec)

#### Functions

-   csvToJson
-   getDrugsFromRules

------------------------------------------------------------------------

### Variables

#### exec

* **exec**: *`any`* = require('child\_process').exec

    *Defined in server/csv/CSVController.ts:6*

------------------------------------------------------------------------

Class: CSVController
--------------------

Controller class to handle different requests for this feature

### Index

#### Methods

-   [getDMEs](#sec:getdmes)
-   [getReports](#sec:getreports)
-   [getRules](#sec:getrules)
-   [getStatus](#sec:getstatus)
-   [uploadReports](#sec:uploadreports)

------------------------------------------------------------------------

### Methods

#### getDMEs

* **getDMEs**(req: *`Request`*, res: *`Response`*, next:
    *`NextFunction`*): `Promise`.&lt;`void`&gt;

    *Defined in server/csv/CSVController.ts:253*

    Retrieve array of severe ADR names.

    **Parameters:**

    | Param | Type | Description | 
    | ------ | ------ | ------ | 
    | req |    `Request` | - | 
    | res | `Response` | - |
    | next | `NextFunction` | - |

    **Returns:** `Promise`.&lt;`void`&gt;

------------------------------------------------------------------------

#### getReports

-   **getReports**(req: *`Request`*, res: *`Response`*, next:
    *`NextFunction`*): `Promise`.&lt;`void`&gt;

    *Defined in server/csv/CSVController.ts:164*

    Retrieve array of reports for a given drug (req.query.drug) or
    interaction (req.query.drug1 and req.query.drug2).

    **Parameters:**

      Param   Type             Description
      ------- ---------------- -------------
      req     `Request`        -
      res     `Response`       -
      next    `NextFunction`   -

    **Returns:** `Promise`.&lt;`void`&gt;

------------------------------------------------------------------------

#### getRules

-   **getRules**(req: *`Request`*, res: *`Response`*, next:
    *`NextFunction`*): `Promise`.&lt;`void`&gt;

    *Defined in server/csv/CSVController.ts:51*

    Get rules, drugs, as well as score and severe ADR count
    distributions in json.

    **Parameters:**

      Param   Type             Description
      ------- ---------------- -------------
      req     `Request`        -
      res     `Response`       -
      next    `NextFunction`   -

    **Returns:** `Promise`.&lt;`void`&gt;

------------------------------------------------------------------------

#### getStatus

-   **getStatus**(req: *`Request`*, res: *`Response`*, next:
    *`NextFunction`*): `Promise`.&lt;`void`&gt;

    *Defined in server/csv/CSVController.ts:237*

    Retrieve status information in json.

    **Parameters:**

      Param   Type             Description
      ------- ---------------- -------------
      req     `Request`        -
      res     `Response`       -
      next    `NextFunction`   -

    **Returns:** `Promise`.&lt;`void`&gt;

------------------------------------------------------------------------

#### uploadReports

-   **uploadReports**(req: *`Request`*, res: *`Response`*, next:
    *`NextFunction`*): `Promise`.&lt;`any`&gt;

    *Defined in server/csv/CSVController.ts:20*

    Used to upload FAERS files to the server. Files are passed in as
    req.files.

    **Parameters:**

      Param   Type             Description
      ------- ---------------- -------------
      req     `Request`        -
      res     `Response`       -
      next    `NextFunction`   -

    **Returns:** `Promise`.&lt;`any`&gt;

------------------------------------------------------------------------

External module: "server/csv/index"
-----------------------------------

### Index

#### Functions

-   [init](#sec:init)

  ------------------------------------------------------------------------

### Functions

#### init

* **init**(app: *`express.Application`*): `void`

    *Defined in server/csv/index.ts:10*

    Initialize the routes and other config in the future such as database
    configuration

    **Parameters:**

    | Param | Type | Description |
    | ------ | ------ | ------ | 
    | app |  `express.Application` | - |

    **Returns:** `void`

------------------------------------------------------------------------

External module: "server/csv/routes"
------------------------------------

### Index

#### Variables

-   [crypto](#sec:const-crypto)

#### Functions

-   [default](#sec:default)

------------------------------------------------------------------------

### Variables

#### &lt;&lt;Const&gt;&gt; crypto
* **crypto**: *`any`* = require('crypto')
    
    *Defined in server/csv/routes.ts:4*

------------------------------------------------------------------------

### Functions

#### default

-   **default**(app: *`express.Application`*): `void`

    *Defined in server/csv/routes.ts:4*

    This is where we register the routes, route middlewares for this
    resource

    **Parameters:**

      Param   Type                    Description
      ------- ----------------------- -------------
      app     `express.Application`   -

    **Returns:** `void`

------------------------------------------------------------------------

External module: "server/index"
-------------------------------

### Index

#### Variables

-   [port](#sec:const-port)
-   [server](#sec:const-server)

#### Functions

-   [normalizePort](#sec:normalizeport)
-   [onError](#sec:onerror)
-   [onListening](#sec:onlistening)

------------------------------------------------------------------------

### Variables

#### &lt;&lt;Const&gt;&gt; port

* **port**: *`string` | `number` | `true` | `false`* =
    normalizePort(process.env.PORT || 3000)

    *Defined in server/index.ts:11*

    Set default port to 3000.

------------------------------------------------------------------------

#### &lt;&lt;Const&gt;&gt; server

-   **server**: *`Server`* = http.createServer(App)

    *Defined in server/index.ts:17*

    Configure server.

------------------------------------------------------------------------

### Functions

#### normalizePort

-   **normalizePort**(val: *`number` | `string`*): `number` | `string` |
    `boolean`

    *Defined in server/index.ts:27*

    Make sure port number is valid.

    **Parameters:**

      Param   Type       Description
      ------- ---------- -------------
      val     `number`   `string`

    **Returns:** `number` | `string` | `boolean`

------------------------------------------------------------------------

#### onError

-   **onError**(error: *`ErrnoException`*): `void`

    *Defined in server/index.ts:43*

    Log error output.

    **Parameters:**

      Param   Type               Description
      ------- ------------------ -------------
      error   `ErrnoException`   -

    **Returns:** `void`

------------------------------------------------------------------------

#### onListening

-   **onListening**(): `void`

    *Defined in server/index.ts:63*

    Print the port that the server is listening on.

    **Returns:** `void`

------------------------------------------------------------------------

