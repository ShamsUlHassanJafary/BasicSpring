import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable, of } from 'rxjs';

import { IGarage, Garage } from 'app/shared/model/garage.model';
import { GarageService } from './garage.service';
import { IGarageAdmin } from 'app/shared/model/garage-admin.model';
import { GarageTypeService } from 'app/entities/garage-type/garage-type.service';
import { GarageAdminService } from 'app/entities/garage-admin/garage-admin.service';
import { flatMap } from 'rxjs/operators';
import { IGarageType } from 'app/shared/model/garage-type.model';

@Component({
  selector: 'jhi-garage-update',
  templateUrl: './garage-update.component.html',
})
export class GarageUpdateComponent implements OnInit {
  isSaving = false;
  garageadmins: IGarageAdmin[] = [];
  garageTypes: IGarageType[] = [];

  editForm = this.fb.group({
    id: [],
    businessName: [],
    lineAddress1: [],
    lineAddress2: [],
    city: [],
    county: [],
    postcode: [],
    country: [],
    businessEmail: [null, [Validators.minLength(5), Validators.maxLength(254), Validators.email]],
    phoneNumber: [null, [Validators.required, Validators.minLength(11), Validators.maxLength(13)]],
    file: [],
    logoUrl: [],
    vatRegistered: [],
    garageType: [null, [Validators.required]],
    // garageAdmin: [],
  });
  imageSrc!: string;
  uploadMessage = 'Choose Image';
  fileLoaded = false;

  constructor(
    protected garageService: GarageService,
    protected garageAdminService: GarageAdminService,
    protected garageTypeService: GarageTypeService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ garage }) => {
      this.updateForm(garage);

      if (garage.id)
        this.garageService
          .retrieveLogo(garage.id)
          .pipe(flatMap(res => of(res.body)))
          .subscribe((f: any) => {
            if (f.data) {
              const reader = new FileReader();
              const file = new File([this.dataURItoBlob(f.data, f.fileType)], f.filename, {
                type: 'image/' + f.fileType,
              });
              this.loadImage(reader, file);
            }
          });

      this.garageTypeService.query().subscribe((res: HttpResponse<IGarageType[]>) => (this.garageTypes = res.body || []));

      // this.garageAdminService.query().subscribe((res: HttpResponse<IGarageAdmin[]>) => (this.garageadmins = res.body || []));
    });
  }

  dataURItoBlob(dataURI: string, type: string): Blob {
    const byteString = window.atob(dataURI);
    const arrayBuffer = new ArrayBuffer(byteString.length);
    const int8Array = new Uint8Array(arrayBuffer);
    for (let i = 0; i < byteString.length; i++) {
      int8Array[i] = byteString.charCodeAt(i);
    }
    const blob = new Blob([int8Array], { type: 'image/' + type });
    return blob;
  }

  onFileChange(event: any): void {
    const reader = new FileReader();

    if (event.target.files && event.target.files.length) {
      const file: File = event.target.files[0];
      this.loadImage(reader, file);
    }
  }

  private loadImage(reader: FileReader, file: File): void {
    reader.readAsDataURL(file);

    reader.onload = () => {
      this.imageSrc = reader.result as string;
      this.uploadMessage = file.name;
      this.fileLoaded = true;
      this.editForm.get('file')!.patchValue(file);
    };
  }

  updateForm(garage: IGarage): void {
    this.editForm.patchValue({
      id: garage.id,
      businessName: garage.businessName,
      lineAddress1: garage.lineAddress1,
      lineAddress2: garage.lineAddress2,
      city: garage.city,
      county: garage.county,
      postcode: garage.postcode,
      country: garage.country,
      businessEmail: garage.businessEmail,
      phoneNumber: garage.phoneNumber,
      file: garage.file,
      logoUrl: garage.logoUrl,
      vatRegistered: garage.vatRegistered,
      garageType: garage.garageType,
      // garageAdmin: garage.garageAdmin,
    });
  }

  deleteFile(): void {
    this.editForm.get('file')!.reset();
    this.imageSrc = '';
    this.uploadMessage = 'Choose Image';
    this.fileLoaded = false;
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const garage = this.createFromForm();
    if (garage.id !== undefined) {
      this.subscribeToSaveResponse(this.garageService.update(garage));
    } else {
      this.subscribeToSaveResponse(this.garageService.create(garage));
    }
  }

  private createFromForm(): IGarage {
    return {
      ...new Garage(),
      id: this.editForm.get(['id'])!.value,
      businessName: this.editForm.get(['businessName'])!.value,
      lineAddress1: this.editForm.get(['lineAddress1'])!.value,
      lineAddress2: this.editForm.get(['lineAddress2'])!.value,
      city: this.editForm.get(['city'])!.value,
      county: this.editForm.get(['county'])!.value,
      postcode: this.editForm.get(['postcode'])!.value,
      country: this.editForm.get(['country'])!.value,
      businessEmail: this.editForm.get(['businessEmail'])!.value,
      phoneNumber: this.editForm.get(['phoneNumber'])!.value,
      file: this.editForm.get(['file'])!.value,
      logoUrl: this.editForm.get(['logoUrl'])!.value,
      vatRegistered: this.editForm.get(['vatRegistered'])!.value,
      garageType: this.editForm.get(['garageType'])!.value,
      // garageAdmin: this.editForm.get(['garageAdmin'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGarage>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: IGarageAdmin): any {
    return item.id;
  }

  garageTypeTrackById(index: number, item: IGarageType): any {
    return item.id;
  }
}
