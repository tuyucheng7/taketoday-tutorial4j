import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {MockBackend} from '@angular/http/testing';
import {BaseRequestOptions, Http} from '@angular/http';
import {DatePipe} from '@angular/common';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs/Rx';
import {DataUtils, DateUtils, JhiLanguageService} from 'ng-jhipster';
import {MockLanguageService} from '../../../helpers/mock-language.service';
import {MockActivatedRoute} from '../../../helpers/mock-route.service';
import {PostDetailComponent} from '../../../../../../main/webapp/app/entities/post/post-detail.component';
import {PostService} from '../../../../../../main/webapp/app/entities/post/post.service';
import {Post} from '../../../../../../main/webapp/app/entities/post/post.model';

describe('Component Tests', () => {

	describe('Post Management Detail Component', () => {
		let comp: PostDetailComponent;
		let fixture: ComponentFixture<PostDetailComponent>;
		let service: PostService;

		beforeEach(async(() => {
			TestBed.configureTestingModule({
				declarations: [PostDetailComponent],
				providers: [
					MockBackend,
					BaseRequestOptions,
					DateUtils,
					DataUtils,
					DatePipe,
					{
						provide: ActivatedRoute,
						useValue: new MockActivatedRoute({id: 123})
					},
					{
						provide: Http,
						useFactory: (backendInstance: MockBackend, defaultOptions: BaseRequestOptions) => {
							return new Http(backendInstance, defaultOptions);
						},
						deps: [MockBackend, BaseRequestOptions]
					},
					{
						provide: JhiLanguageService,
						useClass: MockLanguageService
					},
					PostService
				]
			}).overrideComponent(PostDetailComponent, {
				set: {
					template: ''
				}
			}).compileComponents();
		}));

		beforeEach(() => {
			fixture = TestBed.createComponent(PostDetailComponent);
			comp = fixture.componentInstance;
			service = fixture.debugElement.injector.get(PostService);
		});


		describe('OnInit', () => {
			it('Should call load all on init', () => {
				// GIVEN

				spyOn(service, 'find').and.returnValue(Observable.of(new Post(10)));

				// WHEN
				comp.ngOnInit();

				// THEN
				expect(service.find).toHaveBeenCalledWith(123);
				expect(comp.post).toEqual(jasmine.objectContaining({id: 10}));
			});
		});
	});

});
