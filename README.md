# WorkbenchWizard
This repository is intended for some experimentation with a "workspace wizard" concept.

The basic idea of a workspace wizard is to provide a means of helping the user enhance their
experience with the Eclipse IDE in particular and the Eclipse Community in general.

The current implementation is very much a prototype that just spashes a bunch of ideas on
the screen without much actual intelligence under the covers.

The wizard may, for example, notice that the user is running Eclipse IDE on a workstation
configured with the German locale, but without the German language pack applied. In this 
event, the wizard may offer a one-click option to install the bundles for that locale.

It might also be a good vehicle for enabling the user to easily add Eclipse project features
(e.g. C/C++ support) or provide a handy portal to popular additions from Marketplace.
