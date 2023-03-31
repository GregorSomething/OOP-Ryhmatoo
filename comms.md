### Info
See fail on mõeldud soceti suhtluse kirjeldamiseks.
### Algus
````
Client -> ClientInfo serverile  
Server -> Response... (status 0 - error, status 1 - OK)  
Client -> request/action {  
  code 100 - send message  
    string message
    string channel
  code 110 - request messages
    string channel
    int limit
}
Server -> request/action {  
  code 400 - send message  
    Messsage message
  code 410 - message request response
    int count
    Message[] messages
}
````