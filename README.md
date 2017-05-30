# Harvey

*...the harvester :)*

**under development**

## Goal / Use case

In work I often have the task of searching for the same input on similar websites. Usually I create an application which **reads an input file (e.g. TSV)** and crawls the relevant parts of the site. The input files have almost the same columns and the applications are very similar too.

*Harvey* aims to reduce the work when I need to implement a new crawler similar to the previous ones. It provides an **abstract application structure** which has the necessary default, common behaviour but at the same time it's flexible enough.

It can **save each task's state:** when it was processed, with which version of your crawler implementation. Also *Harvey* can skip automatically those tasks which were already processed by the current version.

So it's basically a **flexible batch framework** with some extra functions.