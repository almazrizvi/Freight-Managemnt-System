import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';

// Ensure the JIT compiler is available when lazy-loading NgModules at runtime.
// For production/AOT builds this import is unnecessary and discouraged.
import '@angular/compiler';

bootstrapApplication(App, appConfig)
  .catch((err) => console.error(err));
