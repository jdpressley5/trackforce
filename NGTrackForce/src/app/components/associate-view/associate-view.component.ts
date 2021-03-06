import { User } from './../../models/user.model';
import { Component, OnInit } from '@angular/core';
import { AssociateService } from '../../services/associate-service/associate.service';
import { AuthenticationService } from '../../services/authentication-service/authentication.service';
import { AutoUnsubscribe } from '../../decorators/auto-unsubscribe.decorator';
import { Associate } from '../../models/associate.model';
import { ActivatedRoute } from '@angular/router';
import { LocalStorageUtils } from '../../constants/local-storage';
import { userInfo } from 'os';

/**
 *@author Michael Tseng
 *
 *@description This is the view for associates only
 *
 */
@Component({
  selector: 'app-associate-view',
  templateUrl: './associate-view.component.html',
  styleUrls: ['./associate-view.component.css']
})
@AutoUnsubscribe
export class AssociateViewComponent implements OnInit {
  public associate: Associate;
  public formOpen = false;
  public newAssociate: Associate;
  public newFirstName: string;
  public newLastName: string;

  public errMsg: string;
  public succMsg: string;
  public user: User;
  public id: number;
  isDataReady = false;

  constructor(
    private associateService: AssociateService,
    private authService: AuthenticationService,
    private activated: ActivatedRoute,
    //private clientService: ClientService
  ) {}

  ngOnInit() {
    this.user = JSON.parse(localStorage.getItem(LocalStorageUtils.CURRENT_USER_KEY));
    this.id = this.user.id;
    this.associateService.getAssociateByUserId(this.id).subscribe(
      data => {
        this.associate = data;
        this.isDataReady = true;
        console.log(data);
      },
      error => {
        console.log('error');
      }
    );
  }

  toggleForm() {
    this.formOpen = !this.formOpen;
  }

  updateInfo() {
    
    if(this.newFirstName || this.newLastName) {
      if(this.newFirstName) {
        this.associate.firstName = this.newFirstName;
      }
      if(this.newLastName) {
        this.associate.lastName = this.newLastName;
      }
    }
    else {
      return;
    }    

    this.associateService.updateAssociate(this.associate).then(() => {
      this.succMsg = 'Information updated';
      this.newFirstName = "";
      this.newLastName = "";
    }).catch((err) => {
      this.newFirstName = "";
      this.newLastName = "";
      if (err.status === 500) {
        this.errMsg = 'There was an error with the server.';
      } else {
        this.errMsg =
          'Something went wrong, your information was not updated.';
      }
    });
  }
}
