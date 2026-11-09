This implementation of the Kata was written by me, Paul Praet, in collaboration with Claude Code.
Nowadays agentic programming is quickly revolutionizing how software is being built and we all have to adapt
(like it or not).
Many of us are still struggling with how to strike the right balance.
The risks of mental erosion, code review burnout or overall disconnect from the code base are genuine.

Therefore, for this project, I heavily lean on [Architecture Decision Records (ADRs)](https://adr.github.io).
It is a documentation practice that records every decision being made.
It is not simply a specification, it rather documents the history of decisions of the project.
It will tell you why a decision was taken, which option out of many was selected or even why something was _not_
done.
These documents teach new developers and agents why things are the way they are.
As both humans and agents are very context driven, such records will be of huge value and I advocate using them
everywhere.
It does not address all of the potential concerns of agentic coding but it will help.

When I started on this Kata, it was my goal to first bring the tooling (Gradle, Java) up to a new, modern level so we
can immediately benefit from the newer tooling. Next, I made sure all tests were in place before I started
refactoring the code base to make it more readable and maintainable.
There, we made the common sense trade-offs (e.g. use of enums) and decisions (e.g. enforcing contracts).

Eventually, I added the required missing piece of functionality which was easy to do given the preparations.

To finish, I asked Claude to do a code review to fix some minor leftovers.

No other implementations of this Kata on the Internet were consulted for this task.

If required, we can further elaborate on this exercise by making a full-blown cloud-hosted webapp of this toy project.
