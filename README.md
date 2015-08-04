# Twotris

This project is a double Tetris game written with ClojureScript/Reagent largely inspired from Timothypratley blog [post](http://timothypratley.blogspot.fr).

## Overview

FIXME: Write a paragraph about the library/project and highlight its goals.

It is written as a single page application to demonstrate how to use Reagent from ClojureScript.

## Setup

To get an interactive development environment run:

    lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).
This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

    lein clean

To create a production build run:

    lein cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL.

## Differences from original code

Original code from timothypratley is hosted on [Github](https://github.com/timothypratley/tetris).

Below, i list the differences from original code.

- 2 Tetris boards at the same time. Just to add some flavor. 
- global variables use ++ syntax. Ex: '+on-tick-interval+'
- local variables are always in uppercase. Ex: 'KEYNAME'


## Todos

Todos list in order of priority :

- change project name to 'twotris'
- **deploy online : game + github**
- fix on-keydown to be a view from model
- azerty/qwerty mode
- write 'overview' section
- add dependency packages diagram
- write 'differences' section
- **blog it**
- **tweet it**
- add 'speed' parameter



## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
