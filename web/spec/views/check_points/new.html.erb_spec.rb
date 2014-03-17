require 'spec_helper'

describe "check_points/new" do
  before(:each) do
    assign(:check_point, stub_model(CheckPoint).as_new_record)
  end

  it "renders new check_point form" do
    render

    # Run the generator again with the --webrat flag if you want to use webrat matchers
    assert_select "form[action=?][method=?]", check_points_path, "post" do
    end
  end
end
