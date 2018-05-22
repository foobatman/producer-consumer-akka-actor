# producer-consumer-akka-actor
A simple implementation of producer/consumer with Akka actors ( !Streams ) 

### Things it handles:
* Multiple consumers
* Non-blocking consumer and producer ( Actors )
* Back pressure
* No locking, synchronization is done via message passing

### Things it doesn't handle:
* **Multiple Producer** - I have left it purposefully because I was thinking about the same pattern that I have used for multiple consumers and extend it for multiple producers. Hence, if I haven't got the multiple consumers correct then I thought there's no point of doing yak shaving. So waiting for valuable feedback.
* **Dead letters and at most once delivery semantics of Akka**: It should be taken into consideration while developing Akka like message passing systems, the failures that can happen when an individual Actor goes down, a message could be sent to actor's ref before actual Actor is deployed by scheduler etc. This is crucial, for instance in my implementation (PFA code) if producer actor is not started and queueActor asks for NextItem then that message is lost and goes into the deadletter queue. The current solution doesn't handle that. But it can be done separately without any change in the whatever logic I have for producer/consumer and queue. I would tackle it by adding DeadLetterQueueMonitor. Or by designing deployment of this system to provide proper ACK / READY messages and then starting consumer / queueActors. I just thought that it's a distinct concern and should tackle it separately. 
(I have tested this scenario and have ensured that in such situations messages do really go into a deadletter queue)
* **Fault Tolerance** - This was more to demostrate synchronization / back-pressure techniques in slow / fast producer-consumers. Hence, havne't paid attention to it. But you are right, ideally, one should always design a topology using `Supervisors` to handle that.

