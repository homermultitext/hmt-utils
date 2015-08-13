---
layout: page
title: "HMT utilities library"
---

A utility library for analyzing editions of texts following the standards of the Homer Multitext project.


It includes classes for:

1. *tokenizing editions according to HMT project conventions*.  From source data in the CITE architecture's tabular format (either as File or String objects), classes in the library generate classifying  analytical tokenizations eliminating ambiguities required by TEI P5's `choice` element.  Current development is focused on the `HmtEditorialTokenization` class; in planning is a parallel `HmtDiplomaticTokenization` class.  The output of this tokenization is a pair of URNS: a CTS URN with subreference identifying the token, and a CITE URN classifying it.
2. *further analyzing and validating the tokenization*.   Depending on the type assigned to each token, methods of the `HmtValidator` class can validate the contents of lexical items in the XML source, and the referential integrity of attribute values for named entities.


## Current status ##

See the series of [milestones](https://github.com/homermultitext/hmt-utils/milestones) in the project issue tracker.  The API docs and live specs on line here may lag the version in the repository as work on hmt-utils is very active in the summer of 2015.


- [API docs](api)
- [live specs](specs/HmtUtils.html)

## Prerequisites for running unit tests ##

The repository includes a suite of unit and acceptance tests.  Some unit tests of the `LexicalValidation` class require an installation of `morpheus`, the Greek morphological parser from the Perseus project. 

The Homer Multitext project virtual machine for editors automatically  builds `morpheus`.  If you clone this repository adjacent to the `morpheus` directory in the VM's `/vagrant` directory, the unit tests will find and run `morpheus` by default in unit tests.  The 2015 HMT VM for editors is available from <https://github.com/homermultitext/vm2015>.