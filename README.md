# AutoRota
This project attempts to automatically assign rotas to NHS doctors, as many are currently assigned manually by healthcare workers and admin employees. The backend is implemented in Java, using the OptaPlanner library, and the frontend using React.

The rotas annd website are currently undergoing validation by NHS doctors in their spare time. The site is only being used for testing the rota creation and so is still under construction!

Note that the website currently prepopulates the Rota creation form with a standard set of rules following NHS guidelines. This is to facilitate testing since we are investigating the optimal combination of weights for the different soft rules. In a release version of the site we would not prepopulate the form (but would provide the ability to save form values for a user) and likely not allow altering of the weights of the soft rules since this may have adverse affects on the created rotas.
