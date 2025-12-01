import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ImportOperation } from '../models/import-operation.model';

@Injectable({
  providedIn: 'root'
})
export class ImportOperationService {
  private apiUrl = 'http://localhost:8080/api/import-operations';

  constructor(private http: HttpClient) {}

  getOperations(): Observable<ImportOperation[]> {
    return this.http.get<ImportOperation[]>(this.apiUrl);
  }
}
