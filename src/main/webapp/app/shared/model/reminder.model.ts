import { Moment } from 'moment';
import { ICustomer } from 'app/shared/model/customer.model';
import { IVehicle } from 'app/shared/model/vehicle.model';
import { IGarage } from 'app/shared/model/garage.model';

export interface IReminder {
  id?: number;
  description?: string;
  eventDate?: Moment;
  enabled?: boolean;
  alertDate?: Moment;
  secondAlertDate?: Moment;
  comment?: string;
  emailEnabled?: boolean;
  smsEnabled?: boolean;
  alertTime?: Moment;
  secondAlertTime?: Moment;
  customer?: ICustomer;
  vehicle?: IVehicle;
  garage?: IGarage;
  reference?: string;
}

export class Reminder implements IReminder {
  constructor(
    public id?: number,
    public description?: string,
    public eventDate?: Moment,
    public enabled?: boolean,
    public alertDate?: Moment,
    public secondAlertDate?: Moment,
    public comment?: string,
    public emailEnabled?: boolean,
    public smsEnabled?: boolean,
    public alertTime?: Moment,
    public secondAlertTime?: Moment,
    public customer?: ICustomer,
    public vehicle?: IVehicle,
    public garage?: IGarage,
    public reference?: string
  ) {
    this.enabled = this.enabled || false;
    this.emailEnabled = this.emailEnabled || false;
    this.smsEnabled = this.smsEnabled || false;
  }
}
