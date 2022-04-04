import { IBooking } from 'app/shared/model/booking.model';
import { IJob } from 'app/shared/model/job.model';
import { IGarageAdmin } from 'app/shared/model/garage-admin.model';
import { IVehicle } from 'app/shared/model/vehicle.model';
import { ICustomer } from 'app/shared/model/customer.model';
import { IGarageType } from 'app/shared/model/garage-type.model';

export interface IGarage {
  id?: number;
  businessName?: string;
  lineAddress1?: string;
  lineAddress2?: string;
  city?: string;
  county?: string;
  postcode?: string;
  country?: string;
  phoneNumber?: string;
  businessEmail?: string;
  bookings?: IBooking[];
  jobs?: IJob[];
  garageAdmins?: IGarageAdmin[];
  vehicles?: IVehicle[];
  customers?: ICustomer[];
  file?: File;
  logoUrl?: string;
  vatRegistered?: boolean;
  garageType?: IGarageType;
}

export class Garage implements IGarage {
  constructor(
    public id?: number,
    public businessName?: string,
    public lineAddress1?: string,
    public lineAddress2?: string,
    public city?: string,
    public county?: string,
    public postcode?: string,
    public country?: string,
    public phoneNumber?: string,
    public businessEmail?: string,
    public bookings?: IBooking[],
    public jobs?: IJob[],
    public garageAdmins?: IGarageAdmin[],
    public vehicles?: IVehicle[],
    public customers?: ICustomer[],
    public file?: File,
    public logoUrl?: string,
    public vatRegistered?: boolean,
    public garageType?: IGarageType
  ) {}
}
