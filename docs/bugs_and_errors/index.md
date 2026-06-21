# Error Tracking
CustomCrafting uses error tracking to assist in the maintenance of the plugin/mod.  
The Error tracking integration makes issues/errors more visible and gives insight on how well the plugin/mod functions in production. 
Which in return enables far quicker bug fixes and improvements than previously possible.

::: tip 

**By using CustomCrafting, you opt into error tracking and accept the below-mentioned data collection.**

**You may opt out by following the [instructions](#opt-out)**

:::

## How does it work?
When an error happens within the plugin/mod, the error and additional context in which the error has happened are sent to a server.   
On the server, the information is collected and presented on a dashboard. 
Given the additional context information, the cause of the issue can be tracked down and fixed a lot easier and quicker.

## What information is collected?
Care needs to be taken because the environment in which the plugin/mod runs may contain personal information. For that reason, CustomCrafting tries to collect as little information as possible while still benefiting the error tracking and development.

### Information collected
Each datapoint contributes to answering a question that helps to solve issues:

* timestamp (UTC) - `when was the error first seen? last seen?`
* minecraft version - `is minecraft version supported?`
* customcrafting version - `which version is affected? latest version? already fixed in later version?`
* platform info (type, version) - e.g. `which platform is affected? e.g. spigot/paper/fabric`
* exception stacktrace - `where in the code did the error happen?`
* list of installed plugins/mods and their version - `are there common plugins/mods? possible conflicts?`
* runtime info (JDK) - `is the jdk version correct/supported?`
* thread name and id `useful for multithreaded scenarios. on which thread did the error occur?`
* breadcrumbs - `set within the code to determine configurations and code paths taken before the error ocurred`

::: tip

I try to be as transparent about this as possible, but it is possible that this list may be incomplete in case I overlooked something.
If you find something that I've missed, please let me know. For example, via an issue on GitHub.

:::

### Information that is NOT collected
* Computer Name, Computer User, or similar Personal Identifiable Information, is not and will never be collected!
* Full Server Logs
* Logs unrelated to CustomCrafting / other projects of mine
* Errors unrelated to CC / other projects of mine

## The Software behind it
The error tracking is possible thanks to the [Sentry SDK](https://github.com/getsentry/sentry-java).  
While [Sentry](https://sentry.io) provides their own cloud services with additional features like performance monitoring, that was deemed unnecessary and too intrusive.   

Instead, CustomCrafting uses a more privacy-focused self-hosted alternative called [Bugsink](https://www.bugsink.com/) purely focused on error tracking.
That way the data, even though it is anonymised, is not handled by any third-parties.

## Opt-Out
A solution to easily opt out of the error tracking via a command in the game or console is in the works.  

You can also disable it by using the following steps:    
1. open the jar archive (using any archive program)  
2. open `com/wolfyscript/customcrafting/vals.properties` 
3. set `sentry.enabled` to `false`
4. save the file and make sure the jar file includes the modifications
