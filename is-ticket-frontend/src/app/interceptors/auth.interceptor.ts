import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Get the auth token from localStorage
  const authToken = localStorage.getItem('token');

  // Clone the request and add the authorization header if token exists
  if (authToken) {
    if (req.url.indexOf('/sign-up') > -1 || req.url.indexOf('/sign-in') > -1) {
      return next(req);
    }

    const authReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${authToken}`)
    });
    return next(authReq);
  }

  // If no token, just pass the original request
  return next(req);
};
