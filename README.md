# benefits-exercise

This is an excercise for a sample benefits application. 

In the essence of time, the code for this specific design is syncronous. 
As I implemented the initial design, I did start working on an event based structure that I would like to discuss the benefits of as well.

There are also a few things that were omitted in the implementation to bring up before discussing the next steps to improve upon this design.

#1 Authn/Authz
In a production application, you can use Spring Security hooked up to an identify provider like Okta, Cognito, or AD to allow a user to authenticate themselves and provide a token to the REST API and allow for role based controls on each endpoint.

#2 Resource Validation / Global Error Handler
Validation to each resource was also omitted, but a production application would be able to validate Create/Update requests to ensure data being saved is as expected. 
In addition to this, a standard set of exceptions that map to HTTP Status codes would also be used to handle common scenarios such a resources not being found translations to 404s
as well as 400/422 errors for failed validations.

#3 Payroll Calculation 
Another step in this design would be to extract the types of Payroll calendars that can be set up. 
Currently, this is hard coded to the values given in the exercise prompt in Payroll Service, but this could be extracted to a class that takes 
in the Payroll Calendar type (ie bi weekly, weekly) and the benefit package (ie $1000 deduction for employees, $500 deduction for dependents) and calculates the employees paycheck from there.
This would allow for calculation of payrolls on different types of intervals and different deduction and salary amounts.

#4 Tests
Tests were omitted for the most part with the exception of the most complication logic for this application in the PayrollService that handles calculating employee payroll.
In a practical scenario unit and integration tests would be built for all non CRUD operations to ensure ease of maintenance and protect ourselves in the future.

Beyond the omission of these, the next step we can take to improve this design is to tackle some of the awkwardness I encountered while implementing the current design of how 
an admin will need to invoke an API to generate payroll and then approve it once verified. 

To work around this, we can emit events when an Employee is created or updated or when a Benefit Package is updated. 
We can then listen into these events to calculate Payroll as these changes are occurring rather than doing a large one time calculation as this would not scale well as payroll size increases.
An admin would then be able to view the current state of payroll, make any adjustments necessary and verify the numbers.
This approach is especially helpful as the payroll data will be eventually consistent at the time of payroll verification.


Swagger URL:
http://localhost:8080/swagger-ui/index.html