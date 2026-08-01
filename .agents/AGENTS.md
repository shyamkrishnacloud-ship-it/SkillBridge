# Rules

- **Routing / Navigation:** The application does not have a public home page mapped to `/`. The default page for authenticated users should always be `/profile`. Future code (e.g. controllers, security configs, redirects after actions) should redirect to `/profile` instead of `/` to prevent 404 errors.
