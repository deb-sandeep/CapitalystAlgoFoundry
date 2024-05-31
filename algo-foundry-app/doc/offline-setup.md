## How to setup CapitalystAlgoFoundry for offline mode

### 0. Clone the Git repo
https://github.com/deb-sandeep/CapitalystAlgoFoundry

### 1. Setting up local database
1. Install any local database (MySQL is preferred)
2. Create a schema 'market_data' by importing the src/sql/localdb.setup.sql *
3. Set up your database password as an environment variable DB_PASSWORD +
3. Modify src/main/resources/application.properties to update the datasource
   url, username and password

### 2. Setting up the local cache
1. Modify the src/main/resources/algo-foundry.properties to update the
   following properties:
    * algofoundry.work-offline=true
    * algofoundry.workspace-path=[working folder for this app]
2. Create a folder 'server-cache' inside the workspace-path directory 
3. Copy the contents of the doc/offline-cache (only the .cache) files 
   to the server-cache folder.

### 3. Execute the app
1. Run the AlgoFoundry class
2. Application has been tested for JDK 17.

----

(*) Note that the localdb is just an empty database. In the offline mode 
  the application uses ONLY cached data. The local database is only for 
  code compatibility reasons.

(+) Setting up the DB password as an environment variable is optional. 
  You can hardcode the password in the configuration in case you are not
  planning to share your codebase.
