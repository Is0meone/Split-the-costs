# Expense Splitter
Application for splitting expenses between multiple people.

## Project Structure

The project is divided into 3 modules:
1. library (lib): the core logic of dividing expenses and should connect to the database
2. api: the REST API that exposes the functionality of the library via HTTP interface. It's the server side of the app.
3. app: the client interface (GUI) that each user will run.

### Introduction to each part:
#### Library

The idea is to use a graph database (Neo4J) and utilize pathfinding to solve for the final debts.

There are 3 entities defined - 1 node and 2 types of edges (relationships):
- User: this is our sole node. We're assuming no email validation is needed and as such other than relationships it contains just an ID, a name and a password hash.
- Friendship: this is a relationship between 2 users. It has a status (pending, accepted, rejected or auto-accepting payment requests as a higher form of accepted.)
- Obligation: this is really the core object of this project. It has an amount, short description, status (pending, accepted, rejected or paid) and a timestamp.

Additionally, there is an ExpenseSplitter helper class for, well, dividing expenses between multiple users (e.g. when a group of friends go out to dinner and want to split the bill automatically).

#### API

The API is a Spring Boot REST application. The 4 primary routes are `/users`, `/obligations`, `/friends` and `/auth` for registration/login.

Quite a bit here will have to be implementation-defined (configurations, security, etc.) - currently it's just controllers and data transfer objects.

#### App 

The frontend in JavaFX and should use the API to get and send all data.

Ideally it'd be an android app, but that's a bit beyond this course :)

You can find a basic UI wireframe [here](https://www.tldraw.com/v/Akd_c_7v9MfgASwS_PhWTqUIPz?viewport=0%2C0%2C1920%2C947&page=page%3Ab7IZwZtfoCV7BiOXsUiDb), in the [`dics-assets/wireframe.tldraw`](docs-assets/wireframe.tldraw) file or as an svg image under [`docs-assets/wireframe.svg`](docs-assets/wireframe.svg):
![basic wireframe](docs-assets/wireframe.svg)

### class diagram

<div class="center" style="height: auto;">

```mermaid
classDiagram
direction LR
class ApiApplication {
  + main(String[]) void
  + securityFilterChain(HttpSecurity) SecurityFilterChain
}
class ApiApplicationTests {
  ~ contextLoads() void
}
class AuthController {
  + login(LoginDTO) UserDTO
  + logout() void
  + register(RegisterDTO) UserDTO
}
class ExpenseSplitter {
  - User actor
  ~ split(Double, List~User~) void
  ~ split(Double, User[]) void
  ~ split(Map~User, Double~) void
   User actor
}
class ExpenseSplitterApplication {
  + start(Stage) void
  + main(String[]) void
}
class Friendship {
  - Long id
  - User user2
  - User user1
  - Status status
  + equals(Object) boolean
  + hashCode() int
  + toString() String
   Long id
   User user1
   Status status
   User user2
}
class FriendshipController {
  + rejectFriendship(Long) void
  + requestOrAcceptFriendship(Long) void
  + markAsAutoAccept(Long) void
   List~FriendshipDTO~ friendshipRequests
   List~UserDTO~ friends
}
class FriendshipDTO
class LoginDTO {
  - String password
  - String name
   String name
   String password
}
class Obligation {
  - Status status
  - String description
  - User creditor
  - Long id
  - LocalDateTime timestamp
  - Double amount
  - User debtor
  + pay() void
  + equals(Object) boolean
  + accept() void
  + decline() void
  + hashCode() int
  + toString() String
   String description
   User creditor
   LocalDateTime timestamp
   Long id
   Double amount
   User debtor
   Status status
}
class ObligationController {
  + getObligationsTo(Long) ObligationsToDTO
  + acceptObligation(Long) void
  + getObligationsFor(Long) List~ObligationWithIdDTO~
  + getObligation(Long) ObligationWithIdDTO
  + requestObligationFrom(Long, ObligationDTO) void
   List~ObligationTotalDTO~ obligationTotals
   List~ObligationWithIdDTO~ pendingObligations
}
class ObligationDTO {
  - String creditorId
  - Status status
  - String description
  - String timestamp
  - String debtorId
  - Double amount
   String description
   String debtorId
   String creditorId
   Double amount
   String timestamp
   Status status
}
class ObligationTotalDTO {
  - User user
  - Double totalAmount
   Double totalAmount
   User user
}
class ObligationWithIdDTO {
  - Long id
   Long id
}
class ObligationsToDTO {
  - List~ObligationWithIdDTO~ obligations
  - Double total
   Double total
   List~ObligationWithIdDTO~ obligations
}
class RegisterDTO {
  - String name
  - String password
   String name
   String password
}
class SplitObligationDTO {
  - Double amount
  - String timestamp
  - String description
  - List~Long~ users
   String description
   List~Long~ users
   Double amount
   String timestamp
}
class SplitObligationManualDTO {
  - String timestamp
  - String description
  - Map~Long, Double~ users
   String description
   Map~Long, Double~ users
   String timestamp
}
class Status {
<<enumeration>>
  + valueOf(String) Status
  + values() Status[]
}
class Status {
<<enumeration>>
  + values() Status[]
  + valueOf(String) Status
}
class User {
  - String passwordHash
  - List~Obligation~ isOwed
  - String name
  - Long id
  - List~Obligation~ owes
  - List~Friendship~ friendsWith
  + requestObligationFrom(User, Double, String, LocalDateTime) void
  + toString() String
  + equals(Object) boolean
  + payObligationTo(User, Long) void
  + acceptObligationTo(User, Long) void
  + hashCode() int
   String name
   String passwordHash
   Long id
   List~Obligation~ isOwed
   List~Friendship~ friendsWith
   List~Obligation~ owes
}
class UserController {
  + getTotalObligationsToTo(Long) String
  + getUser(Long) UserDTO
   List~UserDTO~ users
}
class UserDTO {
  - Long id
  - String name
   String name
   Long id
}
class UserTokenDTO

ExpenseSplitter "1" *--> "actor 1" User 
Friendship "1" *--> "status 1" Status 
Friendship "1" *--> "user1 1" User 
FriendshipDTO "1" *--> "status 1" Status 
FriendshipDTO "1" *--> "from 1" User 
Obligation "1" *--> "status 1" Status 
Obligation "1" *--> "creditor 1" User 
ObligationDTO "1" *--> "status 1" Status 
ObligationTotalDTO "1" *--> "user 1" User 
ObligationWithIdDTO  -->  ObligationDTO 
ObligationsToDTO "1" *--> "obligations *" ObligationWithIdDTO 
Friendship  -->  Status 
Obligation  -->  Status 
User "1" *--> "friendsWith *" Friendship 
User "1" *--> "owes *" Obligation 
```
</div>

An IntelliJ UML version from which this mermaid code was generated is also included as [`class_diagram.uml`](docs-assets/class_diagram.uml).

### Use case diagram

![usecase diagram](docs-assets/usecases.svg)