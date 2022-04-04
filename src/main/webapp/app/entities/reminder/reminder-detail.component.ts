import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IReminder } from 'app/shared/model/reminder.model';

@Component({
  selector: 'jhi-reminder-detail',
  templateUrl: './reminder-detail.component.html',
})
export class ReminderDetailComponent implements OnInit {
  reminder: IReminder | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ reminder }) => (this.reminder = reminder));
  }

  previousState(): void {
    window.history.back();
  }
}
