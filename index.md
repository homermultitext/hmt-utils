---
layout: page
title: "HMT utilities library"
---

A utility library for analyzing editions of texts following the standards of the Homer Multitext project.


It includes classes for:

- tokenizing text according to HMT project conventions.  From source data in the CITE architecture's tabular format (either as File or String objects), it produces tokenizations of coherent editions that eliminate ambiguities required by TEI P5's `choice` element.
- further analyzing the tokenization, depending on the type assigned to each token.


## NB:  current status ##

The library is being extensively revised in preparation for the HMT project's summer workshop at the Center for Hellenic Studies (`0.4.x` version series);  at the moment, the posted specifications and documentations currently lag behind the code in the project's github repository at <https://github.com/homermultitext/hmt-utils>.

- [API docs](api)
- [live specs](specs/HmtUtils.html)