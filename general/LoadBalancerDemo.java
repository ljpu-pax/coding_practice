// A prominent airline just suffered a weather related outage, their legacy system
// had some trouble dealing with things.  Lucky for you, you just started a
// company to make modernized airline reservation software.  So, your job is to
// create a new airline reservation system.  

// Use any language and tooling of your choice, including LLMs, so we can get an
// idea of how you usually work.

// The system should be invocable for testing (e.g. via CLI), and must implement
// these operations:
// - Search for a flight, by any of: departing city, arriving city, flight times.
// - For a specific flight, view available seats.
// - For a specific flight, reserve some number of seats until the customer
// finishes payment.
// - Purchase reserved seat(s).  Assume a stub for now that we could fill in later.
// - Cancel a previously purchased seat(s).

// You can assume a fixed plane dimension, such as 24 rows with 6 seats each.

// You can also assume a fixed list of flights that you can create, for example:
// 2025-03-01 San Francisco -> Portland,
// Departing at 8:45 am, arriving at 10:05 am
