import { IUser } from 'app/core/user/user.model';
import { IBooking } from 'app/shared/model/booking.model';
import { IGarage } from 'app/shared/model/garage.model';
import { IVehicle } from 'app/shared/model/vehicle.model';

export interface ICustomer {
  id?: number;
  hasDataKeepConsent?: boolean;
  hasMarketingContent?: boolean;
  hasNotificationContent?: boolean;
  phoneNumber?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  user?: IUser;
  bookings?: IBooking[];
  garages?: IGarage[];
  vehicles?: IVehicle[];
  searchTag?: string;
  customerToString?: string;
}

export class Customer implements ICustomer {
  constructor(
    public id?: number,
    public hasDataKeepConsent?: boolean,
    public hasMarketingContent?: boolean,
    public hasNotificationContent?: boolean,
    public phoneNumber?: string,
    public firstName?: string,
    public lastName?: string,
    public email?: string,
    public user?: IUser,
    public bookings?: IBooking[],
    public garages?: IGarage[],
    public vehicles?: IVehicle[],
    public searchTag?: string,
    public customerToString?: string
  ) {
    this.hasDataKeepConsent = this.hasDataKeepConsent || false;
    this.hasMarketingContent = this.hasMarketingContent || false;
    this.hasNotificationContent = this.hasNotificationContent || false;
  }
}
