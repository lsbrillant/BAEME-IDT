# TidyBurp

A tagging, filtering, and exporting extension for Burp Suite to streamline request analysis and workflow organization.

## Overview

TidyBurp enhances the Burp Suite experience by allowing users to:

- Filter and search through HTTP requests and responses using rich, regex-supported options
- Organize traffic into custom tabs based on filters
- Annotate requests with predefined and custom tags
- Export filtered traffic to CSV for external analysis

Built for both Burp Suite Community and Professional editions.

## Installation

1. **Clone the repository** and open it in IntelliJ IDEA (or your preferred Java IDE).

2. **Build the JAR file using Gradle**:
   - Open the **Gradle** panel in your IDE
   - Navigate to: `helloburp > Tasks > shadow`
   - Run the `shadowJar` task
   - The built JAR will be located at: `helloburp/build/libs/*-SNAPSHOT-all.jar`

3. **Load the extension in Burp Suite**:
   - Go to the **Extender** tab → **Extensions**
   - Click **Add**
   - Choose **Java** as the extension type
   - Select the generated JAR file (`*-SNAPSHOT-all.jar`)
   - Click **Open**

Once installed, the **TidyBurp** tab should appear in Burp Suite.

## Features

### Filters
Filter HTTP requests using dropdown selectors or a regex-enabled search bar. Filters include:

- Request Metadata: `Number`, `Host`, `Method`, `Request Source`, `Time`, `IP`, `TLS`
- Content: `Header`, `Params`, `Cookies`, `Title`, `Extension`, `MIME`, `Length`
- Response Metadata: `Status`, `Code`, `Edited`
- Tag-based Filtering: `Tags`

### Organization Tabs
Create and manage tabs containing filtered request groups:
- Use the **"Create Tab"** button to generate tabs on the left sidebar
- Each tab stores a filtered subset of requests for better organization

### Tagging System
Label requests using:
- **Premade Tags**: `Baseline`, `Retest`, `Tested`
- **Custom Tags**: Create and save your own using the **"Custom Tag"** button

Tags appear directly in the log table as colored labels.

### Export to CSV
Export your visible filtered requests:
- Click **"Export Visible Requests"** to choose a file path and save request data in `.csv` format

## Developer Documentation

### Requirements

- **Languages**: Java
- **IDE**: IntelliJ IDEA recommended
- **Dependencies**:
  - Montoya API
  - Gradle

### Build Instructions

Follow the same steps as in the [Installation](#installation) section to generate/test JAR

### Repo File Structure

- `helloburp/`
  - `.idea/`
  - `gradle/wrapper/`
    - `gradle-wrapper.jar`
    - `gradle-wrapper.jar.json`
    - `gradle-wrapper.properties`
  - `src/main/java/org/example/`
    - `logtable/`
      - `LogTable.java` – Displays all `LogEntry` records
      - `LogTableColumn.java` – Column UI setup
      - `LogTableColumnModel.java` – Manages column order/values
      - `LogTableController.java` – Controls table data flow
      - `LogTableModel.java` – Injects `LogEntry` into table cells
      - `MultiTagCellRenderer.java` – Renders multiple tags in log table cells
    - `requestviewer/`
      - `RequestViewerController.java` – Connects model and view
      - `RequestViewerPanel.java` – UI for request/response
    - `tabfeature/`
      - `Tab.java`, `TabController.java`, `TabModel.java`, `TabView.java`
    - `AnnotationsTab.java` – Assembles tabbed UI layout
    - `ExportFeature.java` – Exports to CSV
    - `FilterFeature.java` – Handles filter logic
    - `LogEntry.java` – Stores parsed HTTP data
    - `LogHTTPHandler.java` – Processes HTTP transactions
    - `LogProcessor.java` – Single request/response logic
    - `MultipleLogEntryMenu.java` - Right click context menu that appears when multiple log table entries are selected
    - `SingleLogEntryMenu.java` - Right click context menu that appears when a single log table entry is selected
    - `Status.java` – Enum for request status
    - `TaggingFeature.java` – Tag interface and logic
    - `TBContextMenu.java` - Right click context menu in Proxy tab that lets you send requests to TidyBurp
    - `TidyBurp.java` – Main extension class
- Root files: `.gitignore`, `build.gradle.kts`, `settings.gradle.kts`, `gradlew`, `gradlew.bat`

## Team

This project was sponsored by **Security Innovation** and overseen by **Lucien Brilliant** as part of the University of Washington's Informatics Capstone program. The TidyBurp development team consists of:

- **[Sheamin Kim](https://www.linkedin.com/in/sheamink/)** – Product Manager  
- **[Dhruv Ashok](https://www.linkedin.com/in/dhruvashok/)** – Security Engineer  
- **[Iris Hamilton](https://www.linkedin.com/in/iris-ham/)** – UX/UI Designer  
- **[J.R. Lim](https://www.linkedin.com/in/jr-lim/)** – Information Architect  
- **[Michaela Tran](https://www.linkedin.com/in/michaela-tran/)** – Data Engineer

## Acknowledgements

This project was developed as part of a capstone course by students at the University of Washington. We sincerely thank **Security Innovation** for their sponsorship and continued support throughout the development of TidyBurp. Their guidance and feedback were instrumental in helping us build a tool that improves real-world security workflows.
