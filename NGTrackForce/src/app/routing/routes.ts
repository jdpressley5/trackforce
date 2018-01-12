/**
 * @author Antony Lulciuc
 * @description Defines all possible navigatable routes for TrackForce application
 */

import { RouterModule, Routes } from '@angular/router';
import {FormComponent} from "../components/form-component/form.component";
import { HomeComponent } from '../components/home/home.component';
import { ClientListComponent } from '../components/client-list/client-list.component';
import { LoginComponent } from '../components/login/login.component';
import { ClientMappedComponent } from '../components/client-mapped/client-mapped.component';
import { AssociateListComponent } from '../components/associate-list/associate-list.component';
import { BatchListComponent } from '../components/batch-list/batch-list.component';
import { CreateUserComponent } from '../components/create-user/create-user.component';
import { RootComponent } from '../components/root/root.component';

/**
 * Place paths here
 */

export const appRoutes: Routes = [
    {
        path: 'home',
        component: HomeComponent
    },
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'client-listing',
    component: ClientListComponent
  },
    {
        path: 'client-mapped',
        component: ClientMappedComponent
    },
    {
        path: 'associate-listing',
        component: AssociateListComponent
    },
    {
        path: 'batch-listing',
        component: BatchListComponent
    },
    {
        path: 'create-user',
        component: CreateUserComponent
    },
    {
<<<<<<< HEAD
        path: 'form-comp/:id', 
        component: FormComponent 
    },
    
    {
      path: 'login',
      component: LoginComponent
    }


=======
        path: 'root',
        component: RootComponent
      }
>>>>>>> 14bfe3fcaf71d4dcf73705ba445acd8d4c531b35
]


