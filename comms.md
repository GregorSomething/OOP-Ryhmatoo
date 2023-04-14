### Info
See fail on mõeldud soceti suhtluse kirjeldamiseks.
### Algus
100 - 199 -- Client -> Server  
200 - 299 -- Server -> Client
````
100 -> ValidCredentialsRequest -> R200; // Kontrollib parooli õigsust
101 -> CreateNewUserRequest -> R200; // Loob uue kasutaja
102 -> ValidCredentialsRequest -> R200; // Tähendab alustab lugemist sellelt
103 -> ValidCredentialsRequest -> R200; // Tähendab alustab kirjutamist sellelt
109 -> ; // Disconnect

110 -> R210; // Küsis kanalid
111 -> R211; // Küsib aktiivsed kasutajad
112 -> MessageRequest -> R212; // Küsib kanalist sõnumeid

120 -> Message; // Saadab sõnumi
121 -> ChannelCreateRequest; // TODO!!

200 -> LoginResponse; // Väljendab parooli õigsust

210 -> List<Channels>; // Saadab ainult talle nähtavad kanalid
211 -> List<String>; // Saadab aktiivsed kasutajad
212 -> MessageRequestResponse; // Saadab sõnumid, message != null kui error oli, nt vale kanal

220 -> Message; // Saadab just saadetud sõnumi
221 -> Channel; // Saadab just loodud kanali
````