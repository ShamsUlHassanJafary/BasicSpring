import { IUser } from 'app/core/user/user.model';
import { IGarage } from 'app/shared/model/garage.model';

export interface IGarageAdmin {
  id?: number;
  user?: IUser;
  garage?: IGarage;
}

export class GarageAdmin implements IGarageAdmin {
  constructor(public id?: number, public user?: IUser, public garage?: IGarage) {}
}
