# Security

## Reporting a problem

Please email security issues to Shahryartariq57@gmail.com instead of opening a public issue.
I'll reply as soon as I can.

## How the app handles your data

- Sign-in is handled by Firebase Authentication over HTTPS; plain HTTP is disabled.
- Passwords are never stored on the phone or written to logs.
- Plant choices, daily progress and photos stay in the app's private storage, separated per account.
- Phone backups include your plant data but not your sign-in session.
- `google-services.json` and signing keys are never committed; CI reads them from repository secrets.

See [PRIVACY.md](PRIVACY.md) for what is collected.
