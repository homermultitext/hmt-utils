---
layout: page
title: "HMT utilities library"
---

A utility library for analyzing editions of texts following the standards of the Homer Multitext project.


It includes classes for:

1. tokenizing text according to HMT project conventions.  From source data in the CITE architecture's tabular format (either as File or String objects), it produces tokenizations of coherent editions that eliminate ambiguities required by TEI P5's `choice` element.
2. further analyzing the tokenization and validating both the XML text contents and the referential integrity of attribute values according to HMT project conventions, and depending on the type assigned to each token.


## NB:  current status ##

The library is being extensively revised in preparation for the HMT project's summer workshop at the Center for Hellenic Studies (`0.4.x` version series);  at the moment, the posted specifications and documentations currently lag behind the code in the project's github repository at <https://github.com/homermultitext/hmt-utils>.

- [API docs](api)
- [live specs](specs/HmtUtils.html)

## Prerequisites for running unit tests ##

The repository includes a suite of unit and acceptance tests.  Some unit tests of the LexicalValidation class require an installation of `morpheus`, the Greek morphological parser from the Perseus project. 

The Homer Multitext project virtual machine for editors automatically  builds `morpheus`.  If you clone this repository adjacent to the `morpheus` directory in the VM's `/vagrant` directory, the unit tests will find and run `morpheus` by default in unit tests.  The 2015 HMT VM for editors is available from <https://github.com/homermultitext/vm2015>.