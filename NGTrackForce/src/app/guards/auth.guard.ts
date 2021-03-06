import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthenticationService } from '../services/authentication-service/authentication.service';
import { LocalStorageUtils } from '../constants/local-storage';

@Injectable()
/**
 * Controls whether or not the user can actually see a component. If they cannot, they are also redirected by this.
 * Use case: on the component where you want this, in the routes.ts file, simply set the 'canActivate' property to
 * an array that contains this
 **/
export class AuthGuard implements CanActivate {
    // dependency-injecting the router
    constructor(private router: Router, private authService: AuthenticationService) { }
	/**
	 *  The method provided to us by CanActivate interface. Controls whether or not the element with this...canActivate !
	 *  Responsible for redirecting the user to the login page, and maintaining the state of their original request, which,
	 *  when they log in, should be redirected to.
	 */
    async canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        
        // not logged in so redirect to login page with the return url
        if (!localStorage.getItem(LocalStorageUtils.CURRENT_USER_KEY)) {
            this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
            window.alert("test");
            window.alert(localStorage.getItem(LocalStorageUtils.CURRENT_USER_KEY));

            return false;
        }
        const expectedRoles: number[] = route.data.expectedRoles;

        // check of component is restricted by role
        // up refresh getUserRole was undefined. Added line 38-40 to call the database agian upon refresh.
        if (expectedRoles !== undefined) {
            const user = this.authService.getUser();
            if (this.authService.getUserRole() === undefined) {
                await this.authService.getUserRoleFirst((UserRole) => {
                  if (!expectedRoles.includes(UserRole)) {
                    this.routeToUserHome(UserRole);
                    return false;
                  }
              });
            }
        }
        
        return true;
    }

    /**
     * 1806_Austin_M
     * Routes to home page of given user role.
     * @param role user role held in local storage
     */
    routeToUserHome(role: number) {
        console.log(role)
        if (role === 5) {
            this.router.navigate(['associate-view']);
        } else if (role === 2) {
            this.router.navigate(['trainer-view']);
        } else if (role === 1 || role === 3 || role === 4) {
            this.router.navigate(['app-home']);
        }
    }
}
