### Event counter api

- Reads infinite stream of events from the bin file
- Pass the event stream to Akka and group with in every 5 seconds or 1000 records which happens first
- Parse the lines to Event data model and drop the invalid line from streams
- Group by event keys and count number of words
- Expose the count of event name and word count event via rest api

### How to run the application

- Clone the application
- Run the HttpServerApp
- Visit http://localhost:8080/eventCounts
    Response :
```json
{"baz":3,"bar":3}
```

-- after few seconds to see the update counts

```json
{"baz":13,"foo":15,"bar":15}
```