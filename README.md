# flightAgencyAttendanceMonitor


**Project Description**

This project models and simulates the resources and workflow of a flight agency. The operations represented include tasks such as client entry, reservations (handled by the reservation agents), payments, confirmations, and cancellations. Each task is associated with specific delays, simulated using suspension functions (sleep).
The management of shared resources and tasks is performed concurrently through a concurrency monitor. This monitor is designed to work with any Petri net represented as an incidence matrix, accompanied by its initial marking and a custom policy that extends the Policy interface. Additionally, the system allows configuration of alpha and beta times for each transition, providing flexibility in the simulation.
The monitor ensures efficient control of shared resources and organizes the firing order to prevent conflicts. It does so by suspending non-enabled threads attempting to access the monitor and waking them only when appropriate. Access to the monitor from external components is restricted to a single public method:
boolean fireTransition(int transition);
This method manages the attempt to fire a transition and evaluates whether it is enabled at the time of invocation.


**Execution Logging**

The system includes a logging mechanism that captures key execution details:
1. Intermediate States: Periodically, the log records the current marking of the Petri net, indicating the position of clients within the different parts of the network that represent the agency’s operations.
2. Completion: Upon finishing the simulation, the log includes the total time taken to complete all processes.


**Validation Using a Python Automaton**

As part of the execution, the project also launches a Python program that operates as an automaton. This program receives, as a string, the complete sequence of transitions fired during the simulation. The automaton validates whether this sequence is correct according to the rules of the modeled system.
The Python program is executed as a separate process from the Java project itself. Its output is directly printed to the log, appended after the previous entries. This integration transforms the automaton into a built-in testing mechanism that automatically verifies the consistency of the simulated execution.


**Key Features of the Project**

1. Process Simulation:
Models operations: client entry, reservations, payments, confirmations, and cancellations.
Simulates delays using sleep functions.

2. Concurrent Management:
Concurrency monitor to regulate access to shared resources.
Prevention of conflicts in transition firing.

3. Adaptability and Flexible Configuration:
Compatibility with Petri nets defined by incidence matrices.
Support for custom policies via the Policy interface.
Configuration of alpha and beta times for each transition.

4. Comprehensive Logging:
Log that records intermediate states and final simulation results.

5. Automated Validation:
Python automaton to validate the transition sequence.
Integration of the automaton's output into the final log.


**Project Objective**

This project aims to provide an efficient, adaptable, and robust tool for modeling, simulating, and validating concurrent systems based on Petri nets. The inclusion of logging and automatic validation extends its applicability to environments where consistency and process documentation are critical.
